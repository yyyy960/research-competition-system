package com.cms.module.innovation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class InnovationDTO {
    @NotBlank(message = "项目名称不能为空")
    private String projectName;
    private String projectLevel;
    private String projectType;
    private String advisor;
    private String members;
    private LocalDate startTime;
    private Long proposalFileId;
    private Long finalMaterialFileId;
    private Long certificateFileId;
    private List<Long> fileIds;
}
