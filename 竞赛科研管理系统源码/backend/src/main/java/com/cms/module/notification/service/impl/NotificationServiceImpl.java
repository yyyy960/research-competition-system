package com.cms.module.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cms.common.BusinessException;
import com.cms.common.PageResult;
import com.cms.module.notification.entity.Notification;
import com.cms.module.notification.mapper.NotificationMapper;
import com.cms.module.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;

    @Override
    public PageResult<Notification> page(Long userId, int page, int size) {
        QueryWrapper<Notification> qw = new QueryWrapper<>();
        qw.eq("user_id", userId);
        qw.orderByDesc("create_time");

        IPage<Notification> ipage = new Page<>(page, size);
        notificationMapper.selectPage(ipage, qw);

        return PageResult.of(ipage.getTotal(), page, size, ipage.getRecords());
    }

    @Override
    public long unreadCount(Long userId) {
        return notificationMapper.selectUnreadCount(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markRead(Long id) {
        Notification notification = notificationMapper.selectById(id);
        if (notification == null) {
            throw new BusinessException("通知不存在");
        }
        notification.setIsRead(1);
        notificationMapper.updateById(notification);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllRead(Long userId) {
        Notification update = new Notification();
        update.setIsRead(1);
        notificationMapper.update(update,
                new QueryWrapper<Notification>()
                        .eq("user_id", userId)
                        .eq("is_read", 0));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        notificationMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(Long userId) {
        notificationMapper.delete(new QueryWrapper<Notification>().eq("user_id", userId));
    }
}
