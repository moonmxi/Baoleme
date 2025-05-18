package org.demo.baoleme.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public JwtInterceptor() {
        System.out.println("✅ JwtInterceptor 已加载");
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String path = request.getRequestURI();
        System.out.println("进入 JWT 拦截器，路径 = " + path);

        UserHolder.clear();

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            Map<String, Object> payload = JwtUtils.parsePayload(token);
            if (payload != null) {
                Number idNumber = (Number) payload.get("user_id");
                String role = (String) payload.get("role");

                if (idNumber != null && role != null) {
                    // Redis 校验 token 是否存在
                    String redisKey = role + ":token:" + token;
                    Object storedId = redisTemplate.opsForValue().get(redisKey);
                    if (storedId != null) {
                        UserHolder.set(idNumber.longValue(), role);
                        return true;
                    }
                }
            }
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"message\":\"Token 无效或已过期\"}");
        return false;
    }
}