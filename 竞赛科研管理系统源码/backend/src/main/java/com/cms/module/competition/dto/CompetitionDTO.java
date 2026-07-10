package com.cms.module.competition.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CompetitionDTO {

    @NotBlank(message = "竞赛类别不能为空")
    private String competitionCategory;

    @NotBlank(message = "竞赛名称不能为空")
    private String competitionName;

    private String hostUnit;
    private String organizerUnit;
    private String awardUnit;
    private String awardLevel;
    private String awardGrade;
    private LocalDate awardTime;
    private String workName;
    private String advisor;
    private String participants;

    private List<Long> fileIds;
}
