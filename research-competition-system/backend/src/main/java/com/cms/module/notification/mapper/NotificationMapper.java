package com.cms.module.notification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cms.module.notification.entity.Notification;
import org.apache.ibatis.annotations.Select;

public interface NotificationMapper extends BaseMapper<Notification> {
    @Select("SELECT COUNT(*) FROM notification WHERE user_id = #{userId} AND is_read = 0")
    long selectUnreadCount(Long userId);
}
