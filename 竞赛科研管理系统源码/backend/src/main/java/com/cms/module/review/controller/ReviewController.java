package com.cms.module.review.controller;

import com.cms.common.PageResult;
import com.cms.common.Result;
import com.cms.module.review.dto.ReviewDTO;
import com.cms.module.review.service.ReviewService;
import com.cms.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/todo")
    public Result<PageResult<Map<String, Object>>> getTodoList(
            @RequestParam(required = false) String achievementType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        String userRole = SecurityUtils.getCurrentUserRole();
        PageResult<Map<String, Object>> result = reviewService.getTodoList(achievementType, page, size, userRole, status);
        return Result.ok(result);
    }

    @PostMapping("/approve")
    public Result<Void> approve(@RequestBody ReviewDTO dto) {
        reviewService.approve(dto);
        return Result.ok();
    }

    @PostMapping("/reject")
    public Result<Void> reject(@RequestBody ReviewDTO dto) {
        reviewService.reject(dto);
        return Result.ok();
    }
}
