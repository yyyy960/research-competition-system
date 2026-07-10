package com.cms.module.review.service;

import com.cms.module.review.entity.AchievementTimeline;
import java.util.List;

public interface TimelineService {
    List<AchievementTimeline> getTimeline(String type, Long id);
    void addEvent(String type, Long id, String node, String action, String comment);
}
