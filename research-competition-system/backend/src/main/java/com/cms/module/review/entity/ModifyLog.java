package com.cms.module.review.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("achievement_modify_log")
public class ModifyLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String achievementType;
    private Long achievementId;
    private String modifyType;
    private Long operatorId;
    private String operatorName;
    private String beforeData;
    private String afterData;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
