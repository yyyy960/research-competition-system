package com.cms.module.file.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_file")
public class SysFile {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String originalName;
    private String storedName;
    private String filePath;
    private Long fileSize;
    private String fileType;
    private String fileExt;
    private String achievementType;
    private Long achievementId;
    private Long uploadUserId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
