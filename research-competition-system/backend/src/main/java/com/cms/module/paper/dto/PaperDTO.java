package com.cms.module.paper.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class PaperDTO {
    @NotBlank(message = "论文标题不能为空")
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
    private List<Long> fileIds;
}
