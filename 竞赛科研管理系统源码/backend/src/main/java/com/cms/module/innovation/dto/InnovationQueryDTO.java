package com.cms.module.innovation.dto;

import lombok.Data;

@Data
public class InnovationQueryDTO {
    private int page;
    private int size;
    private String projectLevel;
    private String projectType;
    private String status;
    private String keyword;
    private Integer year;
}
