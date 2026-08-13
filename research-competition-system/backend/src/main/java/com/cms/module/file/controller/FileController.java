package com.cms.module.file.controller;

import com.cms.common.PageResult;
import com.cms.common.Result;
import com.cms.module.file.entity.SysFile;
import com.cms.module.file.service.FileService;
import com.cms.security.SecurityUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping("/page")
    public Result<PageResult<SysFile>> page(@RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size,
                                             @RequestParam(required = false) String fileType,
                                             @RequestParam(required = false) String keyword) {
        return Result.ok(fileService.page(page, size, fileType, keyword));
    }

    @PostMapping("/upload")
    public Result<Map<String, Object>> upload(@RequestParam("file") MultipartFile file,
                                               @RequestParam(required = false) String achievementType,
                                               @RequestParam(required = false) Long achievementId) {
        SysFile f = fileService.upload(file, achievementType, achievementId);
        Map<String, Object> data = Map.of(
                "id", f.getId(),
                "originalName", f.getOriginalName(),
                "url", "/api/file/" + f.getId()
        );
        return Result.ok(data);
    }

    @GetMapping("/{id}")
    public void download(@PathVariable Long id, HttpServletResponse response) throws IOException {
        SysFile f = fileService.getById(id);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\""
                + URLEncoder.encode(f.getOriginalName(), StandardCharsets.UTF_8) + "\"");
        Files.copy(Path.of(f.getFilePath()), response.getOutputStream());
    }

    @GetMapping("/preview/{id}")
    public void preview(@PathVariable Long id, HttpServletResponse response) throws IOException {
        SysFile f = fileService.getById(id);
        String contentType = switch (f.getFileExt().toLowerCase()) {
            case "pdf" -> "application/pdf";
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            default -> "application/octet-stream";
        };
        response.setContentType(contentType);
        Files.copy(Path.of(f.getFilePath()), response.getOutputStream());
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        fileService.delete(id);
        return Result.ok();
    }
}
