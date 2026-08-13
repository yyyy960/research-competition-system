package com.cms.module.paper.dto;

import lombok.Data;

@Data
public class PaperQueryDTO {
    private Integer page = 1;
    private Integer size = 10;
    private String journalLevel;
    private String status;
    private String keyword;
    private Integer year;
}
