package com.ailab.config;

import com.ailab.interceptor.TokenInterceptor;
import io.micrometer.common.lang.NonNullApi;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@NonNullApi
@Configuration
@RequiredArgsConstructor
public class MvcConfig implements WebMvcConfigurer {

    // 可以在这里添加一些全局的拦截器、视图解析器等配置

    private final TokenInterceptor tokenInterceptor;

    /**
     * 添加拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/**") // 拦截所有请求
                .excludePathPatterns("/api/auth/login"); // 排除登录接口
    }

    /**
     * 添加CORS配置
     *
     * @param registry
     */
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**") // 允许所有路径
//                .allowedOrigins("http://localhost:6173") // 允许的源，可以指定具体的域名或IP地址
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的HTTP方法
//                .allowedHeaders("*") // 允许所有请求头
//                .allowCredentials(true) // 是否允许发送Cookie
//                .maxAge(3600); // 预检请求的缓存时间，单位为秒
//    }
}
