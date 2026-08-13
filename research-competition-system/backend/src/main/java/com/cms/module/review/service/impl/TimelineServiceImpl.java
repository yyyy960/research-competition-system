package com.cms.module.review.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cms.module.review.entity.AchievementTimeline;
import com.cms.module.review.mapper.AchievementTimelineMapper;
import com.cms.module.review.service.TimelineService;
import com.cms.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TimelineServiceImpl implements TimelineService {

    private final AchievementTimelineMapper timelineMapper;

    @Override
    public List<AchievementTimeline> getTimeline(String type, Long id) {
        QueryWrapper<AchievementTimeline> qw = new QueryWrapper<>();
        qw.eq("achievement_type", type);
        qw.eq("achievement_id", id);
        qw.orderByDesc("create_time");
        return timelineMapper.selectList(qw);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addEvent(String type, Long id, String node, String action, String comment) {
        AchievementTimeline event = new AchievementTimeline();
        event.setAchievementType(type);
        event.setAchievementId(id);
        event.setNode(node);
        event.setAction(action);
        event.setComment(comment);
        event.setOperatorId(SecurityUtils.getCurrentUserId());
        event.setOperatorName(SecurityUtils.getCurrentUsername());
        timelineMapper.insert(event);
    }
}
