package com.cms.module.review.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("achievement_timeline")
public class AchievementTimeline {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String achievementType;
    private Long achievementId;
    private String node;
    private Long operatorId;
    private String operatorName;
    private String action;
    private String comment;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
