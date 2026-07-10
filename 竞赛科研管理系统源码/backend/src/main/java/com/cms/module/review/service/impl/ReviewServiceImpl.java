package com.cms.module.review.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cms.common.BusinessException;
import com.cms.common.PageResult;
import com.cms.module.competition.entity.Competition;
import com.cms.module.competition.mapper.CompetitionMapper;
import com.cms.module.copyright.entity.SoftwareCopyright;
import com.cms.module.copyright.mapper.CopyrightMapper;
import com.cms.module.innovation.entity.InnovationProject;
import com.cms.module.innovation.mapper.InnovationProjectMapper;
import com.cms.module.notification.entity.Notification;
import com.cms.module.notification.mapper.NotificationMapper;
import com.cms.module.paper.entity.AcademicPaper;
import com.cms.module.paper.mapper.PaperMapper;
import com.cms.module.review.dto.ReviewDTO;
import com.cms.module.review.entity.ReviewRecord;
import com.cms.module.review.mapper.ReviewRecordMapper;
import com.cms.module.review.service.ReviewService;
import com.cms.module.review.service.TimelineService;
import com.cms.module.user.entity.SysUser;
import com.cms.module.user.mapper.SysUserMapper;
import com.cms.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final CompetitionMapper competitionMapper;
    private final InnovationProjectMapper innovationMapper;
    private final CopyrightMapper copyrightMapper;
    private final PaperMapper paperMapper;
    private final ReviewRecordMapper reviewRecordMapper;
    private final SysUserMapper sysUserMapper;
    private final NotificationMapper notificationMapper;
    private final TimelineService timelineService;

    @Override
    public PageResult<Map<String, Object>> getTodoList(String achievementType, int page, int size, String userRole, String status) {
        String targetStatus;
        if (status != null && !status.isEmpty()) {
            targetStatus = status;
        } else if ("ROLE_SECRETARY".equals(userRole)) {
            targetStatus = "pending_review";
        } else if ("ROLE_LEADER".equals(userRole)) {
            targetStatus = "under_review";
        } else if ("ROLE_ADMIN".equals(userRole)) {
            targetStatus = null; // Admin sees all non-archived items for full audit capability
        } else if ("ROLE_STUDENT".equals(userRole)) {
            throw new BusinessException("无权限查看待办列表");
        } else {
            throw new BusinessException("无权限查看待办列表");
        }

        List<Map<String, Object>> allRecords = new ArrayList<>();
        boolean isStudent = "ROLE_STUDENT".equals(userRole);
        Long studentUserId = isStudent ? SecurityUtils.getCurrentUserId() : null;

        // Query competition achievements
        if (achievementType == null || "competition".equals(achievementType)) {
            QueryWrapper<Competition> qw = new QueryWrapper<>();
            if (targetStatus != null) qw.eq("status", targetStatus);
            else qw.notIn("status", "archived", "draft");
            if (isStudent) qw.eq("submit_user_id", studentUserId);
            List<Competition> list = competitionMapper.selectList(qw);
            for (Competition c : list) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id", c.getId());
                map.put("type", "competition");
                map.put("title", c.getCompetitionName());
                map.put("workName", c.getWorkName());
                map.put("category", c.getCompetitionCategory());
                map.put("awardLevel", c.getAwardLevel());
                map.put("awardGrade", c.getAwardGrade());
                map.put("advisor", c.getAdvisor());
                map.put("participants", c.getParticipants());
                map.put("submitUserId", c.getSubmitUserId());
                map.put("status", c.getStatus());
                map.put("createTime", c.getCreateTime());
                allRecords.add(map);
            }
        }

        // Query innovation projects
        if (achievementType == null || "innovation".equals(achievementType)) {
            QueryWrapper<InnovationProject> qw = new QueryWrapper<>();
            if (targetStatus != null) qw.eq("status", targetStatus);
            else qw.notIn("status", "archived", "draft");
            if (isStudent) qw.eq("submit_user_id", studentUserId);
            List<InnovationProject> list = innovationMapper.selectList(qw);
            for (InnovationProject p : list) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id", p.getId());
                map.put("type", "innovation");
                map.put("title", p.getProjectName());
                map.put("projectLevel", p.getProjectLevel());
                map.put("projectType", p.getProjectType());
                map.put("advisor", p.getAdvisor());
                map.put("members", p.getMembers());
                map.put("submitUserId", p.getSubmitUserId());
                map.put("status", p.getStatus());
                map.put("createTime", p.getCreateTime());
                allRecords.add(map);
            }
        }

        // Query software copyrights
        if (achievementType == null || "copyright".equals(achievementType)) {
            QueryWrapper<SoftwareCopyright> qw = new QueryWrapper<>();
            if (targetStatus != null) qw.eq("status", targetStatus);
            else qw.notIn("status", "archived", "draft");
            if (isStudent) qw.eq("submit_user_id", studentUserId);
            List<SoftwareCopyright> list = copyrightMapper.selectList(qw);
            for (SoftwareCopyright c : list) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id", c.getId());
                map.put("type", "copyright");
                map.put("title", c.getSoftwareName());
                map.put("registrationNumber", c.getRegistrationNumber());
                map.put("copyrightOwner", c.getCopyrightOwner());
                map.put("submitUserId", c.getSubmitUserId());
                map.put("status", c.getStatus());
                map.put("createTime", c.getCreateTime());
                allRecords.add(map);
            }
        }

        // Query academic papers
        if (achievementType == null || "paper".equals(achievementType)) {
            QueryWrapper<AcademicPaper> qw = new QueryWrapper<>();
            if (targetStatus != null) qw.eq("status", targetStatus);
            else qw.notIn("status", "archived", "draft");
            if (isStudent) qw.eq("submit_user_id", studentUserId);
            List<AcademicPaper> list = paperMapper.selectList(qw);
            for (AcademicPaper p : list) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id", p.getId());
                map.put("type", "paper");
                map.put("title", p.getTitle());
                map.put("journalName", p.getJournalName());
                map.put("journalLevel", p.getJournalLevel());
                map.put("authors", p.getAuthors());
                map.put("submitUserId", p.getSubmitUserId());
                map.put("status", p.getStatus());
                map.put("createTime", p.getCreateTime());
                allRecords.add(map);
            }
        }

        // Sort by createTime descending
        allRecords.sort((a, b) -> {
            LocalDateTime ta = (LocalDateTime) a.get("createTime");
            LocalDateTime tb = (LocalDateTime) b.get("createTime");
            if (ta == null && tb == null) return 0;
            if (ta == null) return 1;
            if (tb == null) return -1;
            return tb.compareTo(ta);
        });

        // Paginate
        int total = allRecords.size();
        int start = (page - 1) * size;
        int end = Math.min(start + size, total);

        if (start >= total) {
            return PageResult.of(total, page, size, Collections.emptyList());
        }

        List<Map<String, Object>> records = allRecords.subList(start, end);
        return PageResult.of(total, page, size, records);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(ReviewDTO dto) {
        String userRole = SecurityUtils.getCurrentUserRole();
        Long userId = SecurityUtils.getCurrentUserId();

        // Update achievement status and get submitUserId
        Long submitUserId = updateAchievementStatus(dto.getAchievementType(), dto.getAchievementId(), userRole);

        // Determine review level
        String reviewLevel;
        if ("ROLE_SECRETARY".equals(userRole)) {
            reviewLevel = "secretary";
        } else if ("ROLE_LEADER".equals(userRole)) {
            reviewLevel = "leader";
        } else {
            reviewLevel = "admin";
        }

        // Create review record
        ReviewRecord record = new ReviewRecord();
        record.setAchievementType(dto.getAchievementType());
        record.setAchievementId(dto.getAchievementId());
        record.setReviewerId(userId);
        record.setReviewLevel(reviewLevel);
        record.setStatus("approved");
        record.setComment(dto.getComment());
        record.setReviewTime(LocalDateTime.now());

        reviewRecordMapper.insert(record);

        // Record timeline event
        timelineService.addEvent(dto.getAchievementType(), dto.getAchievementId(),
                reviewLevel + "_review", "approve", dto.getComment());

        // Create notification for submitter
        Notification submitterNotif = new Notification();
        submitterNotif.setUserId(submitUserId);
        submitterNotif.setRelatedType(dto.getAchievementType());
        submitterNotif.setRelatedId(dto.getAchievementId());

        String achieveName = getAchievementName(dto.getAchievementType(), dto.getAchievementId());
        if ("ROLE_ADMIN".equals(userRole)) {
            // Admin directly archives
            submitterNotif.setTitle("归档通知 - " + achieveName);
            submitterNotif.setContent("您的成果「" + achieveName + "」已被管理员审核归档。");

            notifyRoleUsers("SECRETARY", "归档通知",
                    "成果「" + achieveName + "」已被管理员归档。",
                    dto.getAchievementType(), dto.getAchievementId());
            notifyRoleUsers("LEADER", "归档通知",
                    "成果「" + achieveName + "」已被管理员归档。",
                    dto.getAchievementType(), dto.getAchievementId());
        } else if ("ROLE_LEADER".equals(userRole)) {
            // Leader review: under_review → archived
            submitterNotif.setTitle("归档通知 - " + achieveName);
            submitterNotif.setContent("您的成果「" + achieveName + "」已通过领导审核，已成功归档。");

            notifyRoleUsers("SECRETARY", "归档通知",
                    "成果「" + achieveName + "」已完成领导审核，已归档。",
                    dto.getAchievementType(), dto.getAchievementId());
        } else {
            // Secretary review: pending_review → under_review
            submitterNotif.setTitle("审核通过 - " + achieveName);
            submitterNotif.setContent("您的成果「" + achieveName + "」已通过秘书审核，已进入领导审核阶段。");

            notifyRoleUsers("LEADER", "待审核通知",
                    "成果「" + achieveName + "」已通过秘书审核，请及时处理。",
                    dto.getAchievementType(), dto.getAchievementId());
        }
        submitterNotif.setIsRead(0);
        notificationMapper.insert(submitterNotif);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(ReviewDTO dto) {
        String userRole = SecurityUtils.getCurrentUserRole();
        Long userId = SecurityUtils.getCurrentUserId();

        // Update achievement status to 'returned'
        Long submitUserId = updateAchievementStatusToReturned(dto.getAchievementType(), dto.getAchievementId());

        // Determine review level
        String reviewLevel;
        if ("ROLE_SECRETARY".equals(userRole)) {
            reviewLevel = "secretary";
        } else if ("ROLE_LEADER".equals(userRole)) {
            reviewLevel = "leader";
        } else {
            reviewLevel = "admin";
        }

        // Create review record
        ReviewRecord record = new ReviewRecord();
        record.setAchievementType(dto.getAchievementType());
        record.setAchievementId(dto.getAchievementId());
        record.setReviewerId(userId);
        record.setReviewLevel(reviewLevel);
        record.setStatus("rejected");
        record.setComment(dto.getComment());
        record.setReviewTime(LocalDateTime.now());
        reviewRecordMapper.insert(record);

        // Record timeline event
        timelineService.addEvent(dto.getAchievementType(), dto.getAchievementId(),
                reviewLevel + "_review", "reject", dto.getComment());

        String achieveName = getAchievementName(dto.getAchievementType(), dto.getAchievementId());
        // Create notification for submitter
        Notification notification = new Notification();
        notification.setUserId(submitUserId);
        notification.setTitle("审核退回 - " + achieveName);
        notification.setContent("您的成果「" + achieveName + "」被退回。原因：" + (dto.getComment() != null ? dto.getComment() : "未说明"));
        notification.setRelatedType(dto.getAchievementType());
        notification.setRelatedId(dto.getAchievementId());
        notification.setIsRead(0);
        notificationMapper.insert(notification);
    }

    private Long updateAchievementStatus(String achievementType, Long achievementId, String userRole) {
        // First, look up the achievement to get its current status
        Long submitUserId;
        String currentStatus;
        switch (achievementType) {
            case "competition" -> {
                Competition competition = competitionMapper.selectById(achievementId);
                if (competition == null) throw new BusinessException("竞赛成果不存在");
                currentStatus = competition.getStatus();
                submitUserId = competition.getSubmitUserId();
            }
            case "innovation" -> {
                InnovationProject project = innovationMapper.selectById(achievementId);
                if (project == null) throw new BusinessException("创新项目不存在");
                currentStatus = project.getStatus();
                submitUserId = project.getSubmitUserId();
            }
            case "copyright" -> {
                SoftwareCopyright copyright = copyrightMapper.selectById(achievementId);
                if (copyright == null) throw new BusinessException("软件著作权不存在");
                currentStatus = copyright.getStatus();
                submitUserId = copyright.getSubmitUserId();
            }
            case "paper" -> {
                AcademicPaper paper = paperMapper.selectById(achievementId);
                if (paper == null) throw new BusinessException("论文成果不存在");
                currentStatus = paper.getStatus();
                submitUserId = paper.getSubmitUserId();
            }
            default -> throw new BusinessException("未知的成果类型: " + achievementType);
        }

        // Determine new status based on user role and current status
        String newStatus;
        String requiredCurrentStatus;
        if ("ROLE_SECRETARY".equals(userRole)) {
            newStatus = "under_review";
            requiredCurrentStatus = "pending_review";
        } else if ("ROLE_LEADER".equals(userRole)) {
            newStatus = "archived";
            requiredCurrentStatus = "under_review";
        } else if ("ROLE_ADMIN".equals(userRole)) {
            // Admin has full authority: archive directly from any reviewable status
            if ("pending_review".equals(currentStatus) || "under_review".equals(currentStatus) || "returned".equals(currentStatus)) {
                newStatus = "archived";
                requiredCurrentStatus = null;
            } else if ("archived".equals(currentStatus)) {
                throw new BusinessException("该成果已是归档状态，无需重复审核");
            } else if ("draft".equals(currentStatus)) {
                throw new BusinessException("草稿状态的成果无法审核，请先提交");
            } else {
                throw new BusinessException("当前成果状态为「" + getStatusLabel(currentStatus) + "」，无法审核");
            }
        } else {
            throw new BusinessException("无权限执行审核操作");
        }

        // Validate current status
        if (requiredCurrentStatus != null && !requiredCurrentStatus.equals(currentStatus)) {
            String roleLabel = "ROLE_SECRETARY".equals(userRole) ? "秘书"
                    : "ROLE_LEADER".equals(userRole) ? "领导" : "管理员";
            throw new BusinessException("当前成果状态为「" + getStatusLabel(currentStatus)
                    + "」，" + roleLabel + "只能审核「" + getStatusLabel(requiredCurrentStatus) + "」状态的成果");
        }

        // Now update the achievement status
        switch (achievementType) {
            case "competition" -> {
                Competition competition = competitionMapper.selectById(achievementId);
                competition.setStatus(newStatus);
                competitionMapper.updateById(competition);
            }
            case "innovation" -> {
                InnovationProject project = innovationMapper.selectById(achievementId);
                project.setStatus(newStatus);
                innovationMapper.updateById(project);
            }
            case "copyright" -> {
                SoftwareCopyright copyright = copyrightMapper.selectById(achievementId);
                copyright.setStatus(newStatus);
                copyrightMapper.updateById(copyright);
            }
            case "paper" -> {
                AcademicPaper paper = paperMapper.selectById(achievementId);
                paper.setStatus(newStatus);
                paperMapper.updateById(paper);
            }
        }

        return submitUserId;
    }

    private Long updateAchievementStatusToReturned(String achievementType, Long achievementId) {
        Long submitUserId;
        String currentStatus;
        switch (achievementType) {
            case "competition" -> {
                Competition competition = competitionMapper.selectById(achievementId);
                if (competition == null) throw new BusinessException("竞赛成果不存在");
                currentStatus = competition.getStatus();
                submitUserId = competition.getSubmitUserId();
                competition.setStatus("returned");
                competitionMapper.updateById(competition);
            }
            case "innovation" -> {
                InnovationProject project = innovationMapper.selectById(achievementId);
                if (project == null) throw new BusinessException("创新项目不存在");
                currentStatus = project.getStatus();
                submitUserId = project.getSubmitUserId();
                project.setStatus("returned");
                innovationMapper.updateById(project);
            }
            case "copyright" -> {
                SoftwareCopyright copyright = copyrightMapper.selectById(achievementId);
                if (copyright == null) throw new BusinessException("软件著作权不存在");
                currentStatus = copyright.getStatus();
                submitUserId = copyright.getSubmitUserId();
                copyright.setStatus("returned");
                copyrightMapper.updateById(copyright);
            }
            case "paper" -> {
                AcademicPaper paper = paperMapper.selectById(achievementId);
                if (paper == null) throw new BusinessException("论文成果不存在");
                currentStatus = paper.getStatus();
                submitUserId = paper.getSubmitUserId();
                paper.setStatus("returned");
                paperMapper.updateById(paper);
            }
            default -> throw new BusinessException("未知的成果类型: " + achievementType);
        }

        // Validate: can only reject items that are in a reviewable state
        if (!"pending_review".equals(currentStatus) && !"under_review".equals(currentStatus)) {
            throw new BusinessException("当前成果状态为「" + getStatusLabel(currentStatus)
                    + "」，无法退回。只能退回「待审核」或「审核中」的成果");
        }

        return submitUserId;
    }

    /** Get human-readable status label for error messages */
    private String getStatusLabel(String status) {
        return switch (status) {
            case "draft" -> "草稿";
            case "pending_review" -> "待审核";
            case "under_review" -> "审核中";
            case "returned" -> "已退回";
            case "archived" -> "已归档";
            default -> status;
        };
    }

    /**
     * Send notification to all users with the given role name.
     */
    private String getAchievementName(String type, Long id) {
        return switch (type) {
            case "competition" -> {
                Competition c = competitionMapper.selectById(id);
                yield c != null ? c.getCompetitionName() : "未知竞赛";
            }
            case "innovation" -> {
                InnovationProject p = innovationMapper.selectById(id);
                yield p != null ? p.getProjectName() : "未知项目";
            }
            case "copyright" -> {
                SoftwareCopyright c = copyrightMapper.selectById(id);
                yield c != null ? c.getSoftwareName() : "未知软著";
            }
            case "paper" -> {
                AcademicPaper p = paperMapper.selectById(id);
                yield p != null ? p.getTitle() : "未知论文";
            }
            default -> "未知成果";
        };
    }

    /** Get the current status of an achievement. */
    private String getCurrentStatus(String type, Long id) {
        return switch (type) {
            case "competition" -> {
                Competition c = competitionMapper.selectById(id);
                yield c != null ? c.getStatus() : null;
            }
            case "innovation" -> {
                InnovationProject p = innovationMapper.selectById(id);
                yield p != null ? p.getStatus() : null;
            }
            case "copyright" -> {
                SoftwareCopyright c = copyrightMapper.selectById(id);
                yield c != null ? c.getStatus() : null;
            }
            case "paper" -> {
                AcademicPaper p = paperMapper.selectById(id);
                yield p != null ? p.getStatus() : null;
            }
            default -> null;
        };
    }

    private void notifyRoleUsers(String roleName, String title, String content, String relatedType, Long relatedId) {
        List<SysUser> users = sysUserMapper.selectByRoleName(roleName);
        for (SysUser user : users) {
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
