package com.ailab.interceptor;

import com.ailab.common.constant.AuthTokenConstant;
import com.ailab.common.context.AuthLoginContext;
import com.ailab.common.properties.JwtProperties;
import com.ailab.common.util.JwtUtils;
import com.ailab.pojo.dto.AuthLoginDTO;
import com.ailab.pojo.vo.AuthLoginVO;
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
            return false;
        }

        // 验证token
        try {
            Claims claims = JwtUtils.parseToken(jwtProperties.getSecretKey(), accessToken);
            AuthLoginDTO authLoginDTO = JwtUtils.getObjectFromClaims(claims,
                    AuthTokenConstant.JWT_CLAIMS_USER_INFO, AuthLoginDTO.class);
            if (authLoginDTO == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false; // 如果解析失败，返回401状态码
            }
            // 将用户信息存储到上下文中，方便后续使用
            AuthLoginContext.setLoginInfo(authLoginDTO);
            return true; // 验证通过，放行请求
        } catch (Exception e) {
            // 验证失败，返回401状态码
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理线程变量，避免内存泄漏
        AuthLoginContext.clear();
    }
}
