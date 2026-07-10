package com.cms.config;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("system_log")
public class SysLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String realName;
    private String operation;
    private String module;
    private String action;
    private String params;
    private String ip;
    private String status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
