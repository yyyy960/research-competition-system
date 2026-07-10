package com.cms.module.notification.service;

import com.cms.common.PageResult;
import com.cms.module.notification.entity.Notification;

public interface NotificationService {
    PageResult<Notification> page(Long userId, int page, int size);
    long unreadCount(Long userId);
    void markRead(Long id);
    void markAllRead(Long userId);
    void delete(Long id);
    void deleteAll(Long userId);
}
