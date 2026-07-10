package com.cms.module.review.controller;

import com.cms.common.Result;
import com.cms.module.review.entity.AchievementTimeline;
import com.cms.module.review.service.TimelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timeline")
@RequiredArgsConstructor
public class TimelineController {

    private final TimelineService timelineService;

    @GetMapping("/{type}/{id}")
    public Result<List<AchievementTimeline>> getTimeline(@PathVariable String type, @PathVariable Long id) {
        return Result.ok(timelineService.getTimeline(type, id));
    }
}
