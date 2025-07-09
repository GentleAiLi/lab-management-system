package com.ailab.config;

import com.ailab.interceptor.TokenInterceptor;
import io.micrometer.common.lang.NonNullApi;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
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
                .excludePathPatterns("/api/auth/login",
                        "/api/auth/refresh"); // 排除接口
    }

}
