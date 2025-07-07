package com.ailab.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ailab.jwt")
@Data
public class JwtProperties {

    private String secretKey; // JWT 密钥
    private Long accessTokenExpiration; // 访问令牌过期时间，单位为毫秒
    private String accessTokenName; // 访问令牌名称
    private Long refreshTokenExpiration; // 刷新令牌过期时间，单位为毫秒
    private String refreshTokenName; // 刷新令牌名称
    private String cookieDomain; // Cookie 域名
}
