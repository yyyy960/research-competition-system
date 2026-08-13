package com.cms.module.paper.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("academic_paper")
public class AcademicPaper {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private LocalDate submissionDate;
    private LocalDate acceptanceDate;
    private String journalName;
    private String keywords;
    private String journalLevel;
    private String authors;
    private Long draftFileId;
    private Long finalFileId;
    private Long reviewCommentFileId;
    private String status;
    private Long submitUserId;
    private Integer isPinned;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
