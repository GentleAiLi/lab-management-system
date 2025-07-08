package com.ailab.service.impl;

import com.ailab.common.constant.AuthTokenConstant;
import com.ailab.common.exception.AuthLoginException;
import com.ailab.common.properties.JwtProperties;
import com.ailab.common.util.JwtUtils;
import com.ailab.mapper.UserMapper;
import com.ailab.pojo.domain.User;
import com.ailab.pojo.dto.AuthLoginDTO;
import com.ailab.pojo.vo.AuthLoginVO;
import com.ailab.service.AuthService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final JwtProperties jwtProperties;


    /**
     * 用户登录
     *
     * @param loginInfo
     * @return
     */
    @Override
    public AuthLoginVO login(AuthLoginDTO loginInfo, HttpServletResponse response) {
        // 查询用户信息
        User user = userMapper.selectOne(new LambdaQueryWrapper<>(User.class)
                .eq(User::getAccountName, loginInfo.getAccountName()));

        // 判断账户名和密码是否匹配
        if (user == null || !BCrypt.checkpw(loginInfo.getPassword(), user.getPassword())) {
            throw new AuthLoginException("登录失败，账户名或密码错误");
        }

        // 判断账号是否被禁用
        if (user.getStatus() == 0) {
            throw new AuthLoginException("登录失败，账号已被禁用");
        }

        // 生成访问令牌和刷新令牌
        String accessToken = JwtUtils.createToken(jwtProperties.getSecretKey(),
                jwtProperties.getAccessTokenExpiration(),
                Map.of(AuthTokenConstant.JWT_CLAIMS_USER_INFO, loginInfo));

        String refreshToken = JwtUtils.createToken(jwtProperties.getSecretKey(),
                jwtProperties.getRefreshTokenExpiration(),
                Map.of(AuthTokenConstant.JWT_CLAIMS_USER_INFO, loginInfo));

        // 将刷新令牌存储在 Cookie 中
        JwtUtils.setRefreshTokenCookie(response, refreshToken,
                jwtProperties.getRefreshTokenName(), jwtProperties.getCookieDomain());

        //TODO 将刷新令牌存入redis


        // 返回登录信息
        return AuthLoginVO.builder()
                .accessToken(accessToken)
                .id(user.getId())
                .accountName(user.getAccountName())
                .role(user.getRole())
                .build();
    }
}
