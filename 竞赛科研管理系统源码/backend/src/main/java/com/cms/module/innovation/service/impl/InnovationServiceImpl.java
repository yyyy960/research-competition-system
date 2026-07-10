package com.cms.module.innovation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cms.common.BusinessException;
import com.cms.common.PageResult;
import com.cms.module.file.entity.SysFile;
import com.cms.module.file.mapper.SysFileMapper;
import com.cms.module.innovation.dto.InnovationDTO;
import com.cms.module.innovation.dto.InnovationQueryDTO;
import com.cms.module.innovation.entity.InnovationProject;
import com.cms.module.innovation.mapper.InnovationProjectMapper;
import com.cms.module.innovation.service.InnovationService;
import com.cms.module.innovation.vo.InnovationVO;
import com.cms.module.notification.entity.Notification;
import com.cms.module.notification.mapper.NotificationMapper;
import com.cms.module.review.service.TimelineService;
import com.cms.module.user.entity.SysUser;
import com.cms.module.user.mapper.SysUserMapper;
import com.cms.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InnovationServiceImpl implements InnovationService {

    private final InnovationProjectMapper innovationProjectMapper;
    private final SysUserMapper sysUserMapper;
    private final SysFileMapper sysFileMapper;
    private final TimelineService timelineService;
    private final NotificationMapper notificationMapper;

    @Override
    public PageResult<InnovationVO> page(InnovationQueryDTO query) {
        LambdaQueryWrapper<InnovationProject> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(query.getProjectLevel())) {
            wrapper.eq(InnovationProject::getProjectLevel, query.getProjectLevel());
        }
        if (StringUtils.hasText(query.getProjectType())) {
            wrapper.eq(InnovationProject::getProjectType, query.getProjectType());
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(InnovationProject::getStatus, query.getStatus());
        }
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(InnovationProject::getProjectName, query.getKeyword());
        }
        if (query.getYear() != null) {
            wrapper.apply("YEAR(start_time) = {0}", query.getYear());
        }

        String role = SecurityUtils.getCurrentUserRole();
        if ("ROLE_STUDENT".equals(role)) {
            wrapper.eq(InnovationProject::getSubmitUserId, SecurityUtils.getCurrentUserId());
        }
        wrapper.orderByDesc(InnovationProject::getCreateTime);
        IPage<InnovationProject> page = innovationProjectMapper.selectPage(
                new Page<>(query.getPage(), query.getSize()), wrapper);

        List<InnovationVO> voList = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(page.getTotal(), page.getCurrent(), page.getSize(), voList);
    }

    @Override
    public InnovationVO getDetail(Long id) {
        InnovationProject project = innovationProjectMapper.selectById(id);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }
        return convertToVO(project);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InnovationVO create(InnovationDTO dto) {
        InnovationProject project = new InnovationProject();
        copyDtoToEntity(dto, project);
        project.setSubmitUserId(SecurityUtils.getCurrentUserId());
        project.setStatus("pending_review");

        innovationProjectMapper.insert(project);

        // Associate uploaded files with this project
        associateFiles(project.getId(), dto.getFileIds());

        // Record timeline event
        timelineService.addEvent("innovation", project.getId(), "submitted", "submit", null);

        // Notify secretary role users
        String projName = project.getProjectName();
        notifySecretaries("新成果提交 - " + projName, "学生提交了创新项目「" + projName + "」，请及时审核。",
                "innovation", project.getId());

        return convertToVO(project);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InnovationVO update(Long id, InnovationDTO dto) {
        InnovationProject project = innovationProjectMapper.selectById(id);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }
        if (!"pending_review".equals(project.getStatus()) && !"returned".equals(project.getStatus())) {
            throw new BusinessException("当前状态不允许修改");
        }

        copyDtoToEntity(dto, project);
        innovationProjectMapper.updateById(project);

        // Re-associate files: clear old and set new
        clearFileAssociations(id);
        associateFiles(id, dto.getFileIds());

        return convertToVO(project);
    }

    @Override
    @Transactional
    public void withdraw(Long id) {
        InnovationProject project = innovationProjectMapper.selectById(id);
        if (project == null) throw new BusinessException("项目不存在");
        if (!"pending_review".equals(project.getStatus())) {
            throw new BusinessException("只有待审核状态的成果才能撤回");
        }
        Long userId = SecurityUtils.getCurrentUserId();
        if (!project.getSubmitUserId().equals(userId)) {
            throw new BusinessException("只能撤回自己的成果");
        }
        project.setStatus("withdrawn");
        innovationProjectMapper.updateById(project);

        // Notify secretaries
        String name = project.getProjectName();
        List<SysUser> secretaries = sysUserMapper.selectByRoleName("SECRETARY");
        for (SysUser user : secretaries) {
            Notification notif = new Notification();
            notif.setUserId(user.getId());
            notif.setTitle("成果撤回通知 - " + name);
            notif.setContent("学生已撤回成果「" + name + "」，无需审核。");
            notif.setRelatedType("innovation");
            notif.setRelatedId(project.getId());
            notif.setIsRead(0);
            notificationMapper.insert(notif);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        InnovationProject project = innovationProjectMapper.selectById(id);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }
        String status = project.getStatus();

        // Notify secretaries about deletion if pending_review
        if ("pending_review".equals(status)) {
            String name = project.getProjectName();
            List<SysUser> secretaries = sysUserMapper.selectByRoleName("SECRETARY");
            for (SysUser user : secretaries) {
                Notification notif = new Notification();
                notif.setUserId(user.getId());
                notif.setTitle("成果删除通知 - " + name);
                notif.setContent("学生已删除创新项目「" + name + "」，无需审核。");
                notif.setRelatedType("innovation");
                notif.setRelatedId(null);
                notif.setIsRead(0);
                notificationMapper.insert(notif);
            }
        }

        // Delete associated file records
        LambdaQueryWrapper<SysFile> fileWrapper = new LambdaQueryWrapper<>();
        fileWrapper.eq(SysFile::getAchievementType, "innovation")
                .eq(SysFile::getAchievementId, id);
        sysFileMapper.delete(fileWrapper);

        innovationProjectMapper.deleteById(id);
    }

    private InnovationVO convertToVO(InnovationProject project) {
        InnovationVO vo = new InnovationVO();
        vo.setId(project.getId());
        vo.setProjectName(project.getProjectName());
        vo.setProjectLevel(project.getProjectLevel());
        vo.setProjectType(project.getProjectType());
        vo.setAdvisor(project.getAdvisor());
        vo.setMembers(project.getMembers());
        vo.setStartTime(project.getStartTime());
        vo.setProposalFileId(project.getProposalFileId());
        vo.setFinalMaterialFileId(project.getFinalMaterialFileId());
        vo.setCertificateFileId(project.getCertificateFileId());
        vo.setStatus(project.getStatus());
        vo.setSubmitUserId(project.getSubmitUserId());
        vo.setCreateTime(project.getCreateTime());
        vo.setUpdateTime(project.getUpdateTime());

        // Populate submitUserName
        if (project.getSubmitUserId() != null) {
            var user = sysUserMapper.selectById(project.getSubmitUserId());
            if (user != null) {
                vo.setSubmitUserName(user.getRealName());
            }
        }

        // Populate individual file names
        populateFileNames(vo, project);

        // Populate files list (achievementType = 'innovation')
        LambdaQueryWrapper<SysFile> fileWrapper = new LambdaQueryWrapper<>();
        fileWrapper.eq(SysFile::getAchievementType, "innovation")
                .eq(SysFile::getAchievementId, project.getId());
        List<SysFile> sysFiles = sysFileMapper.selectList(fileWrapper);
        List<Map<String, Object>> fileMapList = sysFiles.stream().map(f -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", f.getId());
            map.put("originalName", f.getOriginalName());
            map.put("storedName", f.getStoredName());
            map.put("filePath", f.getFilePath());
            map.put("fileSize", f.getFileSize());
            map.put("fileType", f.getFileType());
            map.put("fileExt", f.getFileExt());
            return map;
        }).collect(Collectors.toList());
        vo.setFiles(fileMapList);

        return vo;
    }

    private void populateFileNames(InnovationVO vo, InnovationProject project) {
        if (project.getProposalFileId() != null) {
            SysFile file = sysFileMapper.selectById(project.getProposalFileId());
            if (file != null) {
                vo.setProposalFileName(file.getOriginalName());
            }
        }
        if (project.getFinalMaterialFileId() != null) {
            SysFile file = sysFileMapper.selectById(project.getFinalMaterialFileId());
            if (file != null) {
                vo.setFinalMaterialFileName(file.getOriginalName());
            }
        }
        if (project.getCertificateFileId() != null) {
            SysFile file = sysFileMapper.selectById(project.getCertificateFileId());
            if (file != null) {
                vo.setCertificateFileName(file.getOriginalName());
            }
        }
    }

    private void copyDtoToEntity(InnovationDTO dto, InnovationProject entity) {
        entity.setProjectName(dto.getProjectName());
        entity.setProjectLevel(dto.getProjectLevel());
        entity.setProjectType(dto.getProjectType());
        entity.setAdvisor(dto.getAdvisor());
        entity.setMembers(dto.getMembers());
        entity.setStartTime(dto.getStartTime());
        entity.setProposalFileId(dto.getProposalFileId());
        entity.setFinalMaterialFileId(dto.getFinalMaterialFileId());
        entity.setCertificateFileId(dto.getCertificateFileId());
    }

    private void associateFiles(Long projectId, List<Long> fileIds) {
        if (fileIds != null && !fileIds.isEmpty()) {
            for (Long fileId : fileIds) {
                SysFile sysFile = sysFileMapper.selectById(fileId);
                if (sysFile != null) {
                    sysFile.setAchievementType("innovation");
                    sysFile.setAchievementId(projectId);
                    sysFileMapper.updateById(sysFile);
                }
            }
        }
    }

    private void clearFileAssociations(Long projectId) {
        LambdaQueryWrapper<SysFile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysFile::getAchievementType, "innovation")
                .eq(SysFile::getAchievementId, projectId);
        List<SysFile> sysFiles = sysFileMapper.selectList(wrapper);
        for (SysFile file : sysFiles) {
            file.setAchievementType(null);
            file.setAchievementId(null);
            sysFileMapper.updateById(file);
        }
    }

    private void notifySecretaries(String title, String content, String relatedType, Long relatedId) {
        List<SysUser> secretaries = sysUserMapper.selectByRoleName("SECRETARY");
        for (SysUser user : secretaries) {
            Notification notif = new Notification();
            notif.setUserId(user.getId());
            notif.setTitle(title);
            notif.setContent(content);
            notif.setRelatedType(relatedType);
            notif.setRelatedId(relatedId);
            notif.setIsRead(0);
            notificationMapper.insert(notif);
        }
    }
}
