package com.cms.module.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cms.common.BusinessException;
import com.cms.common.PageResult;
import com.cms.module.file.entity.SysFile;
import com.cms.module.file.mapper.SysFileMapper;
import com.cms.module.file.service.FileService;
import com.cms.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final SysFileMapper sysFileMapper;

    private static final String UPLOAD_DIR = "D:/CompetitionResearchManagementSystem/uploads/";

    @Override
    public PageResult<SysFile> page(int page, int size, String fileType, String keyword) {
        LambdaQueryWrapper<SysFile> wrapper = new LambdaQueryWrapper<>();
        if (fileType != null && !fileType.isEmpty()) {
            wrapper.eq(SysFile::getFileType, fileType);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(SysFile::getOriginalName, keyword);
        }
        wrapper.orderByDesc(SysFile::getCreateTime);

        Page<SysFile> result = sysFileMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysFile upload(MultipartFile file, String achievementType, Long achievementId) {
        if (file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        try {
            // Ensure upload directory exists
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalName = file.getOriginalFilename();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase();
            }

            String storedName = UUID.randomUUID().toString() + "." + ext;
            Path targetPath = uploadPath.resolve(storedName);
            file.transferTo(targetPath.toFile());

            // Determine file type
            String fileType = determineFileType(ext);

            SysFile sysFile = new SysFile();
            sysFile.setOriginalName(originalName);
            sysFile.setStoredName(storedName);
            sysFile.setFilePath(targetPath.toString());
            sysFile.setFileSize(file.getSize());
            sysFile.setFileType(fileType);
            sysFile.setFileExt(ext);
            sysFile.setAchievementType(achievementType);
            sysFile.setAchievementId(achievementId);
            sysFile.setUploadUserId(SecurityUtils.getCurrentUserId());

            sysFileMapper.insert(sysFile);
            return sysFile;
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public SysFile getById(Long id) {
        SysFile sysFile = sysFileMapper.selectById(id);
        if (sysFile == null) {
            throw new BusinessException("文件不存在");
        }
        return sysFile;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SysFile sysFile = sysFileMapper.selectById(id);
        if (sysFile == null) {
            throw new BusinessException("文件不存在");
        }

        // Delete physical file
        try {
            Path filePath = Paths.get(sysFile.getFilePath());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("已删除物理文件: {}", sysFile.getFilePath());
            }
        } catch (IOException e) {
            log.warn("删除物理文件失败: {}", sysFile.getFilePath(), e);
        }

        // Delete database record
        sysFileMapper.deleteById(id);
    }

    private String determineFileType(String ext) {
        return switch (ext) {
            case "jpg", "jpeg", "png", "gif", "bmp", "svg", "webp" -> "image";
            case "doc", "docx", "pdf", "xls", "xlsx", "ppt", "pptx", "txt", "md" -> "document";
            case "zip", "rar", "7z", "tar", "gz" -> "archive";
            case "mp4", "avi", "mov", "wmv", "flv", "mkv" -> "video";
            case "mp3", "wav", "flac", "aac" -> "audio";
            default -> "other";
        };
    }
}
