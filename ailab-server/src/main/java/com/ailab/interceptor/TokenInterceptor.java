package com.ailab.interceptor;

import com.ailab.common.constant.AuthConstant;
import com.ailab.common.context.AuthInfoContext;
import com.ailab.common.context.AuthLoginInfo;
import com.ailab.common.properties.JwtProperties;
import com.ailab.common.util.JwtUtils;
import io.jsonwebtoken.Claims;
import io.micrometer.common.lang.NonNullApi;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@NonNullApi
@RequiredArgsConstructor
public class TokenInterceptor implements HandlerInterceptor {

    private final JwtProperties jwtProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            // 当前拦截到的不是动态方法，直接放行
            return true;
        }

        log.info("拦截到请求：{} {}", request.getMethod(), request.getRequestURI());

        String accessToken = request.getHeader(jwtProperties.getAccessTokenName());

        // 检查accessToken是否存在
        if (accessToken == null || accessToken.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            log.warn("未提供访问令牌");
            return false;
        }

        // 验证token
        try {
            Claims claims = JwtUtils.parseToken(accessToken, jwtProperties.getSecretKey());
            AuthLoginInfo authLoginInfo = JwtUtils.getObjectFromClaims(claims,
                    AuthConstant.JWT_CLAIMS_USER_INFO, AuthLoginInfo.class);
            if (authLoginInfo == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                log.warn("访问令牌解析失败，未找到用户信息");
                return false; // 如果解析失败，返回401状态码
            }
            // 将用户信息存储到上下文中，方便后续使用
            AuthInfoContext.setLoginInfo(authLoginInfo);
            return true; // 验证通过，放行请求
        } catch (Exception e) {
            // 验证失败，返回401状态码
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            log.error("访问令牌验证失败: {}", e.getMessage());
            return false;
        }

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理线程变量，避免内存泄漏
        AuthInfoContext.clear();
    }
}
