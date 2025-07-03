// src/main/java/org/demo/baoleme/config/WebConfig.java
package org.demo.baoleme.config;

import org.demo.baoleme.common.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                // 下面把静态资源路径“/images/**”排除掉
                .excludePathPatterns(
                        "/rider/register",
                        "/rider/login",
                        "/merchant/register",
                        "/merchant/login",
                        "/user/register",
                        "/user/login",
                        "/admin/login",
                        "/error",
                        "/images/**"    // 一定要加上这一行
                );
    }
}
