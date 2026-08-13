package com.cms.module.notification.controller;

import com.cms.common.PageResult;
import com.cms.common.Result;
import com.cms.module.notification.entity.Notification;
import com.cms.module.notification.service.NotificationService;
import com.cms.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/page")
    public Result<PageResult<Notification>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.ok(notificationService.page(userId, page, size));
    }

    @GetMapping("/unread-count")
    public Result<Long> unreadCount() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.ok(notificationService.unreadCount(userId));
    }

    @PutMapping("/{id}/read")
    public Result<Void> markRead(@PathVariable Long id) {
        notificationService.markRead(id);
        return Result.ok();
    }

    @PutMapping("/read-all")
    public Result<Void> markAllRead() {
        Long userId = SecurityUtils.getCurrentUserId();
        notificationService.markAllRead(userId);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        notificationService.delete(id);
        return Result.ok();
    }

    @DeleteMapping("/all")
    public Result<Void> deleteAll() {
        Long userId = SecurityUtils.getCurrentUserId();
        notificationService.deleteAll(userId);
        return Result.ok();
    }
}
