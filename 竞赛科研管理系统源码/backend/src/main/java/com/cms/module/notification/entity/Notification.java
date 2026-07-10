package com.cms.module.notification.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("notification")
public class Notification {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private Integer isRead;
    private String relatedType;
    private Long relatedId;
    private String notificationType;
    private LocalDate deadline;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
