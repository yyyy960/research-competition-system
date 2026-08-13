package com.cms.module.paper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ccf_venue")
public class CcfVenue {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String venueType;
    private String area;
    private String level;
    private String abbreviation;
    private String fullName;
    private String publisher;
    private String url;
}
