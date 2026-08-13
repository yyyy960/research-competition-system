package com.cms.module.review.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("review_record")
public class ReviewRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String achievementType;
    private Long achievementId;
    private Long reviewerId;
    private String reviewLevel;
    private String status;
    private String comment;
    private LocalDateTime reviewTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
