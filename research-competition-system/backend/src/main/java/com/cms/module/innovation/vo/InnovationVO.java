package com.cms.module.innovation.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class InnovationVO {
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
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String submitUserName;
    private List<Map<String, Object>> files;
    private String proposalFileName;
    private String finalMaterialFileName;
    private String certificateFileName;
}
