package com.cms.module.paper.vo;

import com.cms.module.file.entity.SysFile;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PaperVO {
    private Long id;
    private String title;
    private LocalDate submissionDate;
    private LocalDate acceptanceDate;
    private String journalName;
    private String keywords;
    private String journalLevel;
    private String authors;
    private Long draftFileId;
    private String draftFileName;
    private Long finalFileId;
    private String finalFileName;
    private Long reviewCommentFileId;
    private String reviewCommentFileName;
    private String status;
    private Long submitUserId;
    private String submitUserName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<SysFile> files;
}
