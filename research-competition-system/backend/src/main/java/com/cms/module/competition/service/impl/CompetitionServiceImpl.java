package com.cms.module.competition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cms.common.BusinessException;
import com.cms.common.PageResult;
import com.cms.module.competition.dto.CompetitionDTO;
import com.cms.module.competition.dto.CompetitionQueryDTO;
import com.cms.module.competition.entity.Competition;
import com.cms.module.competition.mapper.CompetitionMapper;
import com.cms.module.competition.service.CompetitionService;
import com.cms.module.competition.vo.CompetitionVO;
import com.cms.module.file.entity.SysFile;
import com.cms.module.file.mapper.SysFileMapper;
import com.cms.module.notification.entity.Notification;
import com.cms.module.notification.mapper.NotificationMapper;
import com.cms.module.review.service.TimelineService;
import com.cms.module.user.entity.SysUser;
import com.cms.module.user.mapper.SysUserMapper;
import com.cms.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompetitionServiceImpl implements CompetitionService {

    private final CompetitionMapper competitionMapper;
    private final SysUserMapper sysUserMapper;
    private final SysFileMapper sysFileMapper;
    private final TimelineService timelineService;
    private final NotificationMapper notificationMapper;

    @Override
    public PageResult<CompetitionVO> page(CompetitionQueryDTO query) {
        int pageNum = query.getPage() != null ? query.getPage() : 1;
        int pageSize = query.getSize() != null ? query.getSize() : 10;

        LambdaQueryWrapper<Competition> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(query.getCompetitionCategory())) {
            wrapper.eq(Competition::getCompetitionCategory, query.getCompetitionCategory());
        }
        if (StringUtils.hasText(query.getAwardLevel())) {
            wrapper.eq(Competition::getAwardLevel, query.getAwardLevel());
        }
        if (StringUtils.hasText(query.getAwardGrade())) {
            wrapper.eq(Competition::getAwardGrade, query.getAwardGrade());
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(Competition::getStatus, query.getStatus());
        }
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and(w -> w.like(Competition::getCompetitionName, query.getKeyword())
                              .or()
                              .like(Competition::getWorkName, query.getKeyword()));
        }
        if (query.getYear() != null) {
            wrapper.apply("YEAR(award_time) = {0}", query.getYear());
        }

        // Student can only see own submissions
        String role = SecurityUtils.getCurrentUserRole();
        if ("ROLE_STUDENT".equals(role)) {
            wrapper.eq(Competition::getSubmitUserId, SecurityUtils.getCurrentUserId());
        }

        wrapper.orderByDesc(Competition::getCreateTime);

        Page<Competition> page = competitionMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        List<CompetitionVO> voList = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(page.getTotal(), page.getCurrent(), page.getSize(), voList);
    }

    @Override
    public CompetitionVO getDetail(Long id) {
        Competition competition = competitionMapper.selectById(id);
        if (competition == null) {
            throw new BusinessException("竞赛记录不存在");
        }
        return convertToVO(competition);
    }

    @Override
    @Transactional
    public CompetitionVO create(CompetitionDTO dto) {
        Competition competition = new Competition();
        BeanUtils.copyProperties(dto, competition);
        competition.setSubmitUserId(SecurityUtils.getCurrentUserId());
        competition.setStatus("pending_review");

        competitionMapper.insert(competition);

        // Update file associations if fileIds are provided
        updateFileAssociations(competition.getId(), dto.getFileIds());

        // Record timeline event
        timelineService.addEvent("competition", competition.getId(), "submitted", "submit", null);

        // Notify secretary role users
        String compName = competition.getCompetitionName();
        notifySecretaries("新成果提交 - " + compName, "学生提交了竞赛成果「" + compName + "」，请及时审核。",
                "competition", competition.getId());

        return convertToVO(competition);
    }

    @Override
    @Transactional
    public CompetitionVO update(Long id, CompetitionDTO dto) {
        Competition competition = competitionMapper.selectById(id);
        if (competition == null) {
            throw new BusinessException("竞赛记录不存在");
        }

        String status = competition.getStatus();
        if (!"pending_review".equals(status) && !"returned".equals(status)) {
            throw new BusinessException("当前状态不允许编辑");
        }

        BeanUtils.copyProperties(dto, competition);
        competition.setId(id);

        competitionMapper.updateById(competition);

        // Update file associations if fileIds are provided
        updateFileAssociations(id, dto.getFileIds());

        return convertToVO(competition);
    }

    @Override
    @Transactional
    public void withdraw(Long id) {
        Competition competition = competitionMapper.selectById(id);
        if (competition == null) throw new BusinessException("竞赛记录不存在");
        if (!"pending_review".equals(competition.getStatus())) {
            throw new BusinessException("只有待审核状态的成果才能撤回");
        }
        Long userId = SecurityUtils.getCurrentUserId();
        if (!competition.getSubmitUserId().equals(userId)) {
            throw new BusinessException("只能撤回自己的成果");
        }
        competition.setStatus("withdrawn");
        competitionMapper.updateById(competition);

        // Notify secretaries
        String name = competition.getCompetitionName();
        notifySecretaries("成果撤回通知", "学生已撤回竞赛成果「" + name + "」，无需审核。",
                "competition", competition.getId());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Competition competition = competitionMapper.selectById(id);
        if (competition == null) {
            throw new BusinessException("竞赛记录不存在");
        }

        String status = competition.getStatus();

        // Notify secretaries if pending_review
        String name = competition.getCompetitionName();
        if ("pending_review".equals(status)) {
            notifySecretaries("成果删除通知", "学生已删除竞赛成果「" + name + "」，无需审核。",
                    "competition", null);
        }

        competitionMapper.deleteById(id);

        // Delete associated files
        LambdaQueryWrapper<SysFile> fileWrapper = new LambdaQueryWrapper<>();
        fileWrapper.eq(SysFile::getAchievementType, "competition")
                .eq(SysFile::getAchievementId, id);
        sysFileMapper.delete(fileWrapper);
    }

    /**
     * Convert Competition entity to CompetitionVO with populated submitUserName and files.
     */
    private CompetitionVO convertToVO(Competition competition) {
        CompetitionVO vo = new CompetitionVO();
        BeanUtils.copyProperties(competition, vo);

        // Populate submitUserName
        if (competition.getSubmitUserId() != null) {
            SysUser user = sysUserMapper.selectById(competition.getSubmitUserId());
            if (user != null) {
                vo.setSubmitUserName(user.getRealName());
            }
        }

        // Populate files
        LambdaQueryWrapper<SysFile> fileWrapper = new LambdaQueryWrapper<>();
        fileWrapper.eq(SysFile::getAchievementType, "competition")
                .eq(SysFile::getAchievementId, competition.getId());
        List<SysFile> sysFiles = sysFileMapper.selectList(fileWrapper);

        List<Map<String, Object>> fileList = sysFiles.stream().map(file -> {
            Map<String, Object> fileMap = new LinkedHashMap<>();
            fileMap.put("id", file.getId());
            fileMap.put("originalName", file.getOriginalName());
            fileMap.put("fileSize", file.getFileSize());
            fileMap.put("fileExt", file.getFileExt());
            fileMap.put("fileType", file.getFileType());
            return fileMap;
        }).collect(Collectors.toList());

        vo.setFiles(fileList);

        return vo;
    }

    /**
     * Update file records (sys_file) to associate them with the competition achievement.
     * First clear existing associations, then set new ones if fileIds are provided.
     */
    /**
     * Send notification to all users with SECRETARY role.
     */
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

    private void updateFileAssociations(Long achievementId, List<Long> fileIds) {
        // Clear existing associations for this achievement
        LambdaQueryWrapper<SysFile> clearWrapper = new LambdaQueryWrapper<>();
        clearWrapper.eq(SysFile::getAchievementType, "competition")
                .eq(SysFile::getAchievementId, achievementId);
        sysFileMapper.delete(clearWrapper);

        if (fileIds != null && !fileIds.isEmpty()) {
            List<SysFile> filesToUpdate = sysFileMapper.selectBatchIds(fileIds);
            for (SysFile file : filesToUpdate) {
                file.setAchievementType("competition");
                file.setAchievementId(achievementId);
                sysFileMapper.updateById(file);
            }
        }
    }
}
