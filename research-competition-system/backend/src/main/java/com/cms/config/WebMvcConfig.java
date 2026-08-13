package com.cms.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 * 将 /uploads/** 映射到配置的上传目录，供前端访问已上传的文件。
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final FileUploadProperties fileUploadProperties;

    /**
     * 注册静态资源处理器，把上传目录暴露在 /uploads/** 路径下。
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(fileUploadProperties.getResourceLocation());
    }
}
