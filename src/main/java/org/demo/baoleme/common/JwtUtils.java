package org.demo.baoleme.common;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用 JWT 工具类，支持多角色身份认证
 */
public class JwtUtils {

    private static final String SECRET = "baoleme_secret_key_1234567890123456"; // 至少 32 字符
    private static final long EXPIRE_TIME = 1000 * 60 * 60 * 24;

    private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    public static String createToken(Long userId, String role, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", userId);
        claims.put("role", role);
        claims.put("username", username);

        long now = System.currentTimeMillis();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + EXPIRE_TIME))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static Map<String, Object> parsePayload(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Map<String, Object> payload = new HashMap<>();
            payload.put("user_id", claims.get("user_id", Number.class));
            payload.put("role", claims.get("role", String.class));
            payload.put("username", claims.get("username", String.class));
            return payload;
        } catch (JwtException e) {
            return null;
        }
    }

    public static boolean isExpired(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}