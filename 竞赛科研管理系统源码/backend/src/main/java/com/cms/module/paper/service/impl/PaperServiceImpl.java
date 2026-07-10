package com.cms.module.paper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cms.common.BusinessException;
import com.cms.common.PageResult;
import com.cms.module.paper.dto.PaperDTO;
import com.cms.module.paper.dto.PaperQueryDTO;
import com.cms.module.paper.entity.AcademicPaper;
import com.cms.module.paper.mapper.PaperMapper;
import com.cms.module.paper.service.PaperService;
import com.cms.module.paper.vo.PaperVO;
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
public class PaperServiceImpl implements PaperService {

    private final PaperMapper paperMapper;
    private final SysFileMapper sysFileMapper;
    private final SysUserMapper sysUserMapper;
    private final TimelineService timelineService;
    private final NotificationMapper notificationMapper;

    @Override
    public PageResult<PaperVO> page(PaperQueryDTO queryDTO) {
        LambdaQueryWrapper<AcademicPaper> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(queryDTO.getStatus()), AcademicPaper::getStatus, queryDTO.getStatus());
        wrapper.eq(StringUtils.hasText(queryDTO.getJournalLevel()), AcademicPaper::getJournalLevel, queryDTO.getJournalLevel());
        wrapper.like(StringUtils.hasText(queryDTO.getKeyword()), AcademicPaper::getTitle, queryDTO.getKeyword());
        if (queryDTO.getYear() != null) {
            wrapper.apply("YEAR(create_time) = {0}", queryDTO.getYear());
        }
        String role = SecurityUtils.getCurrentUserRole();
        if ("ROLE_STUDENT".equals(role)) {
            wrapper.eq(AcademicPaper::getSubmitUserId, SecurityUtils.getCurrentUserId());
        }
        wrapper.orderByDesc(AcademicPaper::getCreateTime);
        Page<AcademicPaper> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        Page<AcademicPaper> result = paperMapper.selectPage(page, wrapper);

        List<PaperVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), voList);
    }

    @Override
    public PaperVO getDetail(Long id) {
        AcademicPaper entity = paperMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("学术论文记录不存在");
        }
        return convertToVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(PaperDTO paperDTO) {
        AcademicPaper entity = new AcademicPaper();
        entity.setTitle(paperDTO.getTitle());
        entity.setSubmissionDate(paperDTO.getSubmissionDate());
        entity.setAcceptanceDate(paperDTO.getAcceptanceDate());
        entity.setJournalName(paperDTO.getJournalName());
        entity.setKeywords(paperDTO.getKeywords());
        entity.setJournalLevel(paperDTO.getJournalLevel());
        entity.setAuthors(paperDTO.getAuthors());
        entity.setDraftFileId(paperDTO.getDraftFileId());
        entity.setFinalFileId(paperDTO.getFinalFileId());
        entity.setReviewCommentFileId(paperDTO.getReviewCommentFileId());
        entity.setStatus("pending_review");
        entity.setSubmitUserId(SecurityUtils.getCurrentUserId());

        paperMapper.insert(entity);

        if (paperDTO.getFileIds() != null && !paperDTO.getFileIds().isEmpty()) {
            associateFiles(paperDTO.getFileIds(), entity.getId());
        }

        // Record timeline event
        timelineService.addEvent("paper", entity.getId(), "submitted", "submit", null);

        // Notify secretary role users
        String paperTitle = entity.getTitle();
        notifySecretaries("新成果提交 - " + paperTitle, "学生提交了学术论文「" + paperTitle + "」，请及时审核。",
                "paper", entity.getId());

        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, PaperDTO paperDTO) {
        AcademicPaper entity = paperMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("学术论文记录不存在");
        }
        if (!"pending_review".equals(entity.getStatus()) && !"returned".equals(entity.getStatus())) {
            throw new BusinessException("当前状态不允许编辑");
        }

        entity.setTitle(paperDTO.getTitle());
        entity.setSubmissionDate(paperDTO.getSubmissionDate());
        entity.setAcceptanceDate(paperDTO.getAcceptanceDate());
        entity.setJournalName(paperDTO.getJournalName());
        entity.setKeywords(paperDTO.getKeywords());
        entity.setJournalLevel(paperDTO.getJournalLevel());
        entity.setAuthors(paperDTO.getAuthors());
        entity.setDraftFileId(paperDTO.getDraftFileId());
        entity.setFinalFileId(paperDTO.getFinalFileId());
        entity.setReviewCommentFileId(paperDTO.getReviewCommentFileId());

        paperMapper.updateById(entity);

        // Re-associate files
        disassociateFiles(id);
        if (paperDTO.getFileIds() != null && !paperDTO.getFileIds().isEmpty()) {
            associateFiles(paperDTO.getFileIds(), id);
        }
    }

    @Override
    @Transactional
    public void withdraw(Long id) {
        AcademicPaper entity = paperMapper.selectById(id);
        if (entity == null) throw new BusinessException("学术论文记录不存在");
        if (!"pending_review".equals(entity.getStatus())) {
            throw new BusinessException("只有待审核状态的成果才能撤回");
        }
        Long userId = SecurityUtils.getCurrentUserId();
        if (!entity.getSubmitUserId().equals(userId)) {
            throw new BusinessException("只能撤回自己的成果");
        }
        entity.setStatus("withdrawn");
        paperMapper.updateById(entity);

        // Notify secretaries
        String name = entity.getTitle();
        List<SysUser> secretaries = sysUserMapper.selectByRoleName("SECRETARY");
        for (SysUser user : secretaries) {
            Notification notif = new Notification();
            notif.setUserId(user.getId());
            notif.setTitle("成果撤回通知 - " + name);
            notif.setContent("学生已撤回成果「" + name + "」，无需审核。");
            notif.setRelatedType("paper");
            notif.setRelatedId(entity.getId());
            notif.setIsRead(0);
            notificationMapper.insert(notif);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        AcademicPaper entity = paperMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("学术论文记录不存在");
        }
        String status = entity.getStatus();

        // Notify secretaries about deletion if pending_review
        if ("pending_review".equals(status)) {
            String name = entity.getTitle();
            List<SysUser> secretaries = sysUserMapper.selectByRoleName("SECRETARY");
            for (SysUser user : secretaries) {
                Notification notif = new Notification();
                notif.setUserId(user.getId());
                notif.setTitle("成果删除通知 - " + name);
                notif.setContent("学生已删除学术论文「" + name + "」，无需审核。");
                notif.setRelatedType("paper");
                notif.setRelatedId(null);
                notif.setIsRead(0);
                notificationMapper.insert(notif);
            }
        }

        disassociateFiles(id);
        paperMapper.deleteById(id);
    }

    private PaperVO convertToVO(AcademicPaper entity) {
        PaperVO vo = new PaperVO();
        vo.setId(entity.getId());
        vo.setTitle(entity.getTitle());
        vo.setSubmissionDate(entity.getSubmissionDate());
        vo.setAcceptanceDate(entity.getAcceptanceDate());
        vo.setJournalName(entity.getJournalName());
        vo.setKeywords(entity.getKeywords());
        vo.setJournalLevel(entity.getJournalLevel());
        vo.setAuthors(entity.getAuthors());
        vo.setDraftFileId(entity.getDraftFileId());
        vo.setFinalFileId(entity.getFinalFileId());
        vo.setReviewCommentFileId(entity.getReviewCommentFileId());
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

        // Look up draft file name
        if (entity.getDraftFileId() != null) {
            SysFile file = sysFileMapper.selectById(entity.getDraftFileId());
            if (file != null) {
                vo.setDraftFileName(file.getOriginalName());
            }
        }

        // Look up final file name
        if (entity.getFinalFileId() != null) {
            SysFile file = sysFileMapper.selectById(entity.getFinalFileId());
            if (file != null) {
                vo.setFinalFileName(file.getOriginalName());
            }
        }

        // Look up review comment file name
        if (entity.getReviewCommentFileId() != null) {
            SysFile file = sysFileMapper.selectById(entity.getReviewCommentFileId());
            if (file != null) {
                vo.setReviewCommentFileName(file.getOriginalName());
            }
        }

        // Look up associated files
        List<SysFile> files = sysFileMapper.selectList(
                new LambdaQueryWrapper<SysFile>()
                        .eq(SysFile::getAchievementType, "paper")
                        .eq(SysFile::getAchievementId, entity.getId())
        );
        vo.setFiles(files != null ? files : Collections.emptyList());

        return vo;
    }

    private void associateFiles(List<Long> fileIds, Long achievementId) {
        for (Long fileId : fileIds) {
            SysFile file = sysFileMapper.selectById(fileId);
            if (file != null) {
                file.setAchievementType("paper");
                file.setAchievementId(achievementId);
                sysFileMapper.updateById(file);
            }
        }
    }

    private void disassociateFiles(Long achievementId) {
        List<SysFile> files = sysFileMapper.selectList(
                new LambdaQueryWrapper<SysFile>()
                        .eq(SysFile::getAchievementType, "paper")
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
