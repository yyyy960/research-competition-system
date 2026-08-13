package com.cms.module.copyright.dto;

import lombok.Data;

@Data
public class CopyrightQueryDTO {
    private Integer page = 1;
    private Integer size = 10;
    private String status;
    private String keyword;
    private Integer year;
}
