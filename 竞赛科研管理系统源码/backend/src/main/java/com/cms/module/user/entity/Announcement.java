package com.cms.module.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("announcement")
public class Announcement {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String content;
    private String publisher;
    private LocalDate publishTime;
    private Integer isTop;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
