// src/main/java/org/demo/baoleme/config/WebMvcConfig.java
package org.demo.baoleme.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.storage.upload-dir}")
    private String uploadDir;

    @Value("${file.storage.base-url}")
    private String baseUrl;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 访问 /images/** 时，去 file:uploadDir/** 目录下找文件
        // 注意 uploadDir 前面要加上 file:
        String location = "file:" + uploadDir + "/";
        registry.addResourceHandler(baseUrl + "**")  // 即 "/images/**"
                .addResourceLocations(location);
    }
}
