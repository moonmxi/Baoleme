package org.demo.baoleme.config;

import org.demo.baoleme.common.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/rider/register",
                        "/rider/login",
                        "/admin/login",
                        "/merchant/register",
                        "/merchant/login",
                        "/user/register",
                        "/user/login",
                        "/error"
                );
    }
}