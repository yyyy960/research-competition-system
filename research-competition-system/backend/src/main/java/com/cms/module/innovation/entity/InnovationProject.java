package com.cms.module.innovation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("innovation_project")
public class InnovationProject {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String projectName;
    private String projectLevel;
    private String projectType;
    private String advisor;
    private String members;
    private LocalDate startTime;
    private Long proposalFileId;
    private Long finalMaterialFileId;
    private Long certificateFileId;
    private String status;
    private Long submitUserId;
    private Integer isPinned;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
