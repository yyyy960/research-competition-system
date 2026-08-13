package com.cms.module.copyright.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("software_copyright")
public class SoftwareCopyright {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String softwareName;
    private String organization;
    private String copyrightOwner;
    private String registrationNumber;
    private LocalDate registrationDate;
    private Long certificateFileId;
    private String status;
    private Long submitUserId;
    private Integer isPinned;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
