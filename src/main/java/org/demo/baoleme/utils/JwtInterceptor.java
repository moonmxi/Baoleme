package org.demo.baoleme.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // 清除 ThreadLocal 避免数据残留
        UserHolder.clear();

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            Map<String, Object> payload = JwtUtils.parsePayload(token);
            if (payload != null) {
                Number idNumber = (Number) payload.get("user_id");
                String role = (String) payload.get("role");
                if (idNumber != null && role != null) {
                    UserHolder.set(idNumber.longValue(), role);
                    return true;
                }
            }
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }
}