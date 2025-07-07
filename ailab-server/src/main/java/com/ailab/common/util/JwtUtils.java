package com.ailab.common.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.security.Key;
import java.util.Date;
import java.util.Map;

public class JwtUtils {

    /**
     * 生成token
     *
     * @param secretKey  秘钥
     * @param expireTime token过期时间
     * @param data       设置的信息
     * @return token
     */
    public static <T> String createToken(String secretKey, Long expireTime, Map<String, Object> data) {
        // 当前时间
        Date now = new Date();
        // 过期时间
        Date expiration = new Date(now.getTime() + expireTime);

        JwtBuilder builder = Jwts.builder()
                .setClaims(data) // 设置载荷
                .setIssuedAt(now)  // 设置签发时间
                .setExpiration(expiration)  // 设置过期时间
                .signWith(getKey(secretKey), SignatureAlgorithm.HS256);  // 设置签名

        return builder.compact();
    }

    /**
     * 解析token
     *
     * @param secretKey 秘钥
     * @param token     加密的token
     * @return 设置的信息
     */
    public static Claims parseToken(String secretKey, String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey(secretKey))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 获取签名密钥
     *
     * @param secretKey
     * @return
     */
    private static Key getKey(String secretKey) {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * 从Claims中获取对象
     *
     * @param claims
     * @param key
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T getObjectFromClaims(Claims claims, String key, Class<T> type) {
        Object value = claims.get(key);
        if (value instanceof Map) {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.convertValue(value, type);
        }
        return null;
    }

    /**
     * 设置刷新令牌到Cookie中
     *
     * @param response
     * @param refreshToken
     * @param cookieName
     * @param cookieDomain
     */
    public static void setRefreshTokenCookie(HttpServletResponse response, String refreshToken,
                                             String cookieName, String cookieDomain) {
        Cookie refreshTokenCookie = new Cookie(cookieName, refreshToken);
        refreshTokenCookie.setDomain(cookieDomain); // 设置Cookie的域名
        refreshTokenCookie.setHttpOnly(true); // 设置为HttpOnly，防止JavaScript访问
        // refreshTokenCookie.setSecure(true); // 只能在 HTTPS 下传输
        refreshTokenCookie.setPath("/api/auth");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 设置过期时间为7天
        response.addCookie(refreshTokenCookie);
    }
}
