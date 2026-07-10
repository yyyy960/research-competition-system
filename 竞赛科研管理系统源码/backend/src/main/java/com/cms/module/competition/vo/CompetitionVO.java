package com.cms.module.competition.vo;

import com.cms.module.file.entity.SysFile;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class CompetitionVO {
    private Long id;
    private String competitionCategory;
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
    private String status;
    private Long submitUserId;
    private String submitUserName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private List<Map<String, Object>> files;
}
