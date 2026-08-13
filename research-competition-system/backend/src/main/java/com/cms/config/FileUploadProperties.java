package com.cms.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件上传路径配置
 * <p>
 * 集中读取 application.yml 中的 {@code file.upload.path}，统一解析为绝对路径，
 * 供 {@link com.cms.module.file.service.impl.FileServiceImpl}（写盘）和
 * {@link WebMvcConfig}（静态资源映射）共用，消除硬编码绝对路径带来的环境耦合。
 */
@Slf4j
@Component
@Getter
public class FileUploadProperties {

    /** application.yml 配置的原始路径，支持相对路径（如 ./uploads/），默认 ./uploads/ */
    @Value("${file.upload.path:./uploads/}")
    private String rawPath;

    /** 解析后的绝对路径（无末尾分隔符），供文件 IO 使用 */
    private String absolutePath;

    /**
     * 启动时把 rawPath 解析为绝对路径；解析失败则回退到默认 ./uploads/。
     */
    @PostConstruct
    public void init() {
        try {
            this.absolutePath = Paths.get(rawPath).toAbsolutePath().normalize().toString();
        } catch (Exception e) {
            log.error("解析文件上传路径失败，回退到默认 ./uploads/: {}", rawPath, e);
            this.absolutePath = Path.of("./uploads/").toAbsolutePath().normalize().toString();
        }
    }

    /**
     * 返回 Spring 资源映射可用的 file: URL（正斜杠 + 末尾斜杠）。
     */
    public String getResourceLocation() {
        String normalized = absolutePath.replace('\\', '/');
        if (!normalized.endsWith("/")) {
            normalized += "/";
        }
        return "file:" + normalized;
    }
}
