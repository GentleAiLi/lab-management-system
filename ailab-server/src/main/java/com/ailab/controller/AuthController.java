package com.ailab.controller;

import com.ailab.common.result.ResponseResult;
import com.ailab.pojo.dto.AuthLoginDTO;
import com.ailab.pojo.vo.AuthLoginVO;
import com.ailab.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    protected ResponseResult<AuthLoginVO> login(@RequestBody AuthLoginDTO loginInfo, HttpServletResponse response) {
        log.info("用户登录请求：{}", loginInfo);
        AuthLoginVO authLoginVO = authService.login(loginInfo, response);
        return ResponseResult.success(authLoginVO);
    }

    @GetMapping("/logout")
    protected ResponseResult<Void> logout() {
        log.info("用户登出请求");
        authService.logout();
        return ResponseResult.success();
    }

    @GetMapping("/refresh")
    protected ResponseResult<AuthLoginVO> refresh(HttpServletRequest request) {
        log.info("用户刷新访问令牌请求");
        AuthLoginVO authLoginVO = authService.refreshAccessToken(request);
        return ResponseResult.success(authLoginVO);
    }

}
