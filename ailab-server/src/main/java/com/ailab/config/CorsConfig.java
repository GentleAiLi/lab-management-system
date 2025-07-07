package com.ailab.config;

import lombok.Data;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "ailab.cors")
@Data
public class CorsConfig {

    private String mapping;
    private String[] allowedOrigins;
    private String[] allowedMethods;
    private String allowedHeaders;
    private boolean allowCredentials;
    private long maxAge;

    @Bean
    public CorsFilter coreFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of(allowedOrigins)); // 允许的源
        corsConfiguration.setAllowedMethods(List.of(allowedMethods)); // 允许的HTTP方法
        corsConfiguration.addAllowedHeader(allowedHeaders); // 允许的请求头
        corsConfiguration.setAllowCredentials(allowCredentials); // 是否允许发送Cookie
        corsConfiguration.setMaxAge(maxAge); // 预检请求的缓存时间，单位为秒

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(mapping, corsConfiguration); // 注册CORS配置到指定的路径

        return new CorsFilter(source);
    }
}
