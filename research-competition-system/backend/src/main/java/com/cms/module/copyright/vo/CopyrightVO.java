package com.cms.module.copyright.vo;

import com.cms.module.file.entity.SysFile;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CopyrightVO {
    private Long id;
    private String softwareName;
    private String organization;
    private String copyrightOwner;
    private String registrationNumber;
    private LocalDate registrationDate;
    private Long certificateFileId;
    private String certificateFileName;
    private String status;
    private Long submitUserId;
    private String submitUserName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<SysFile> files;
}
