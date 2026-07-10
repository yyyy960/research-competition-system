package com.cms.module.copyright.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cms.common.BusinessException;
import com.cms.common.PageResult;
import com.cms.module.copyright.dto.CopyrightDTO;
import com.cms.module.copyright.dto.CopyrightQueryDTO;
import com.cms.module.copyright.entity.SoftwareCopyright;
import com.cms.module.copyright.mapper.CopyrightMapper;
import com.cms.module.copyright.service.CopyrightService;
import com.cms.module.copyright.vo.CopyrightVO;
import com.cms.module.file.entity.SysFile;
import com.cms.module.file.mapper.SysFileMapper;
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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CopyrightServiceImpl implements CopyrightService {

    private final CopyrightMapper copyrightMapper;
    private final SysFileMapper sysFileMapper;
    private final SysUserMapper sysUserMapper;
    private final TimelineService timelineService;
    private final NotificationMapper notificationMapper;

    @Override
    public PageResult<CopyrightVO> page(CopyrightQueryDTO queryDTO) {
        LambdaQueryWrapper<SoftwareCopyright> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(queryDTO.getStatus()), SoftwareCopyright::getStatus, queryDTO.getStatus());
        wrapper.like(StringUtils.hasText(queryDTO.getKeyword()), SoftwareCopyright::getSoftwareName, queryDTO.getKeyword());
        if (queryDTO.getYear() != null) {
            wrapper.apply("YEAR(create_time) = {0}", queryDTO.getYear());
        }
        String role = SecurityUtils.getCurrentUserRole();
        if ("ROLE_STUDENT".equals(role)) {
            wrapper.eq(SoftwareCopyright::getSubmitUserId, SecurityUtils.getCurrentUserId());
        }
        wrapper.orderByDesc(SoftwareCopyright::getCreateTime);
        Page<SoftwareCopyright> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        Page<SoftwareCopyright> result = copyrightMapper.selectPage(page, wrapper);

        List<CopyrightVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), voList);
    }

    @Override
    public CopyrightVO getDetail(Long id) {
        SoftwareCopyright entity = copyrightMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("软件著作权记录不存在");
        }
        return convertToVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(CopyrightDTO copyrightDTO) {
        SoftwareCopyright entity = new SoftwareCopyright();
        entity.setSoftwareName(copyrightDTO.getSoftwareName());
        entity.setOrganization(copyrightDTO.getOrganization());
        entity.setCopyrightOwner(copyrightDTO.getCopyrightOwner());
        entity.setRegistrationNumber(copyrightDTO.getRegistrationNumber());
        entity.setRegistrationDate(copyrightDTO.getRegistrationDate());
        entity.setCertificateFileId(copyrightDTO.getCertificateFileId());
        entity.setStatus("pending_review");
        entity.setSubmitUserId(SecurityUtils.getCurrentUserId());

        copyrightMapper.insert(entity);

        if (copyrightDTO.getFileIds() != null && !copyrightDTO.getFileIds().isEmpty()) {
            associateFiles(copyrightDTO.getFileIds(), entity.getId());
        }

        // Record timeline event
        timelineService.addEvent("copyright", entity.getId(), "submitted", "submit", null);

        // Notify secretary role users
        String swName = entity.getSoftwareName();
        notifySecretaries("新成果提交 - " + swName, "学生提交了软件著作权「" + swName + "」，请及时审核。",
                "copyright", entity.getId());

        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, CopyrightDTO copyrightDTO) {
        SoftwareCopyright entity = copyrightMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("软件著作权记录不存在");
        }
        if (!"pending_review".equals(entity.getStatus()) && !"returned".equals(entity.getStatus())) {
            throw new BusinessException("当前状态不允许编辑");
        }

        entity.setSoftwareName(copyrightDTO.getSoftwareName());
        entity.setOrganization(copyrightDTO.getOrganization());
        entity.setCopyrightOwner(copyrightDTO.getCopyrightOwner());
        entity.setRegistrationNumber(copyrightDTO.getRegistrationNumber());
        entity.setRegistrationDate(copyrightDTO.getRegistrationDate());
        entity.setCertificateFileId(copyrightDTO.getCertificateFileId());

        copyrightMapper.updateById(entity);

        // Re-associate files
        disassociateFiles(id);
        if (copyrightDTO.getFileIds() != null && !copyrightDTO.getFileIds().isEmpty()) {
            associateFiles(copyrightDTO.getFileIds(), id);
        }
    }

    @Override
    @Transactional
    public void withdraw(Long id) {
        SoftwareCopyright entity = copyrightMapper.selectById(id);
        if (entity == null) throw new BusinessException("软件著作权记录不存在");
        if (!"pending_review".equals(entity.getStatus())) {
            throw new BusinessException("只有待审核状态的成果才能撤回");
        }
        Long userId = SecurityUtils.getCurrentUserId();
        if (!entity.getSubmitUserId().equals(userId)) {
            throw new BusinessException("只能撤回自己的成果");
        }
        entity.setStatus("withdrawn");
        copyrightMapper.updateById(entity);

        // Notify secretaries
        String name = entity.getSoftwareName();
        List<SysUser> secretaries = sysUserMapper.selectByRoleName("SECRETARY");
        for (SysUser user : secretaries) {
            Notification notif = new Notification();
            notif.setUserId(user.getId());
            notif.setTitle("成果撤回通知 - " + name);
            notif.setContent("学生已撤回成果「" + name + "」，无需审核。");
            notif.setRelatedType("copyright");
            notif.setRelatedId(entity.getId());
            notif.setIsRead(0);
            notificationMapper.insert(notif);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SoftwareCopyright entity = copyrightMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("软件著作权记录不存在");
        }
        String status = entity.getStatus();

        // Notify secretaries about deletion if pending_review
        if ("pending_review".equals(status)) {
            String name = entity.getSoftwareName();
            List<SysUser> secretaries = sysUserMapper.selectByRoleName("SECRETARY");
            for (SysUser user : secretaries) {
                Notification notif = new Notification();
                notif.setUserId(user.getId());
                notif.setTitle("成果删除通知 - " + name);
                notif.setContent("学生已删除软件著作权「" + name + "」，无需审核。");
                notif.setRelatedType("copyright");
                notif.setRelatedId(null);
                notif.setIsRead(0);
                notificationMapper.insert(notif);
            }
        }

        disassociateFiles(id);
        copyrightMapper.deleteById(id);
    }

    private CopyrightVO convertToVO(SoftwareCopyright entity) {
        CopyrightVO vo = new CopyrightVO();
        vo.setId(entity.getId());
        vo.setSoftwareName(entity.getSoftwareName());
        vo.setOrganization(entity.getOrganization());
        vo.setCopyrightOwner(entity.getCopyrightOwner());
        vo.setRegistrationNumber(entity.getRegistrationNumber());
        vo.setRegistrationDate(entity.getRegistrationDate());
        vo.setCertificateFileId(entity.getCertificateFileId());
        vo.setStatus(entity.getStatus());
        vo.setSubmitUserId(entity.getSubmitUserId());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());

        // Look up submit user name
        if (entity.getSubmitUserId() != null) {
            SysUser user = sysUserMapper.selectById(entity.getSubmitUserId());
            if (user != null) {
                vo.setSubmitUserName(user.getRealName());
            }
        }

        // Look up certificate file name
        if (entity.getCertificateFileId() != null) {
            SysFile certFile = sysFileMapper.selectById(entity.getCertificateFileId());
            if (certFile != null) {
                vo.setCertificateFileName(certFile.getOriginalName());
            }
        }

        // Look up associated files
        List<SysFile> files = sysFileMapper.selectList(
                new LambdaQueryWrapper<SysFile>()
                        .eq(SysFile::getAchievementType, "copyright")
                        .eq(SysFile::getAchievementId, entity.getId())
        );
        vo.setFiles(files != null ? files : Collections.emptyList());

        return vo;
    }

    private void associateFiles(List<Long> fileIds, Long achievementId) {
        for (Long fileId : fileIds) {
            SysFile file = sysFileMapper.selectById(fileId);
            if (file != null) {
                file.setAchievementType("copyright");
                file.setAchievementId(achievementId);
                sysFileMapper.updateById(file);
            }
        }
    }

    private void disassociateFiles(Long achievementId) {
        List<SysFile> files = sysFileMapper.selectList(
                new LambdaQueryWrapper<SysFile>()
                        .eq(SysFile::getAchievementType, "copyright")
                        .eq(SysFile::getAchievementId, achievementId)
        );
        for (SysFile file : files) {
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
