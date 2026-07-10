package com.cms.module.copyright.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class CopyrightDTO {
    @NotBlank(message = "软件名称不能为空")
    private String softwareName;
    private String organization;
    private String copyrightOwner;
    private String registrationNumber;
    private LocalDate registrationDate;
    private Long certificateFileId;
    private List<Long> fileIds;
}
