// src/main/java/org/demo/baoleme/config/WebMvcConfig.java
package org.demo.baoleme.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // 从 application.yml 读取的本地存储根目录（“upload”）
    @Value("${file.storage.upload-dir}")
    private String uploadDir;

    // 从 application.yml 读取的外部访问前缀（“/images/”）
    @Value("${file.storage.base-url}")
    private String baseUrl;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 在浏览器里访问 /images/** 时，去 file:uploadDir/** 目录下找文件
        // 注意：一定要在 uploadDir 前面加上 "file:" 前缀，告诉 Spring 这是“文件系统”而非 classpath
        String location = "file:" + uploadDir + "/";
        registry.addResourceHandler(baseUrl + "**")
                .addResourceLocations(location);
    }
}
