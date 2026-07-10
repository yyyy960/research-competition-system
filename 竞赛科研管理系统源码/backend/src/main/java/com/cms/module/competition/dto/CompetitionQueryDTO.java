package com.cms.module.competition.dto;

import lombok.Data;

@Data
public class CompetitionQueryDTO {
    private Integer page;
    private Integer size;
    private String competitionCategory;
    private String awardLevel;
    private String awardGrade;
    private String status;
    private String keyword;
    private Integer year;
}
