package com.cms.module.file.service;

import com.cms.common.PageResult;
import com.cms.module.file.entity.SysFile;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    PageResult<SysFile> page(int page, int size, String fileType, String keyword);
    SysFile upload(MultipartFile file, String achievementType, Long achievementId);
    SysFile getById(Long id);
    void delete(Long id);
}
