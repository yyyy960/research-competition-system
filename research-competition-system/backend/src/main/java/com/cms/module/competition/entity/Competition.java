package com.cms.module.competition.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("competition_achievement")
public class Competition {
    @TableId(type = IdType.AUTO)
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
    private Integer isPinned;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
