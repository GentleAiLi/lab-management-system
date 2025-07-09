package com.ailab.service.impl;

import com.ailab.common.constant.AuthConstant;
import com.ailab.common.context.AuthInfoContext;
import com.ailab.common.context.AuthLoginInfo;
import com.ailab.common.enums.RedisEnum;
import com.ailab.common.enums.UserStatusEnum;
import com.ailab.common.exception.AuthException;
import com.ailab.common.properties.JwtProperties;
import com.ailab.common.util.JwtUtils;
import com.ailab.mapper.UserMapper;
import com.ailab.pojo.domain.User;
import com.ailab.pojo.dto.AuthLoginDTO;
import com.ailab.pojo.vo.AuthLoginVO;
import com.ailab.service.AuthService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final JwtProperties jwtProperties;
    private final StringRedisTemplate stringRedisTemplate;


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
                .select(User::getId, User::getPassword, User::getRole, User::getStatus)
                .eq(User::getAccountName, loginInfo.getAccountName()));

        // 判断账户名和密码是否匹配
        if (user == null || !BCrypt.checkpw(loginInfo.getPassword(), user.getPassword())) {
            throw new AuthException(AuthConstant.LOGIN_FAILED_MESSAGE);
        }

        // 判断账号是否被禁用
        if (user.getStatus() == UserStatusEnum.DISABLED) {
            throw new AuthException(AuthConstant.LOGIN_DISABLED_MESSAGE);
        }

        // 生成访问令牌和刷新令牌
        AuthLoginInfo authLoginInfo = AuthLoginInfo.builder()
                .id(user.getId())
                .accountName(loginInfo.getAccountName())
                .role(user.getRole())
                .build();

        String accessToken = JwtUtils.createToken(jwtProperties.getSecretKey(),
                jwtProperties.getAccessTokenExpiration(),
                Map.of(AuthConstant.JWT_CLAIMS_USER_INFO, authLoginInfo));

        String refreshToken = JwtUtils.createToken(jwtProperties.getSecretKey(),
                jwtProperties.getRefreshTokenExpiration(),
                Map.of(AuthConstant.JWT_CLAIMS_USER_INFO, authLoginInfo));

        // 将刷新令牌存储在 Cookie 中
        JwtUtils.setRefreshTokenToCookie(response, refreshToken,
                jwtProperties.getRefreshTokenName(), jwtProperties.getCookieDomain());

        // 将刷新令牌存入redis
        stringRedisTemplate.opsForValue().set(RedisEnum.USER_REFRESH_TOKEN.getKey() + user.getId(),
                refreshToken, RedisEnum.USER_REFRESH_TOKEN.getTtl(), RedisEnum.USER_REFRESH_TOKEN.getTimeUnit());

        // 返回登录信息
        return AuthLoginVO.builder()
                .accessToken(accessToken)
                .id(user.getId())
                .accountName(user.getAccountName())
                .role(user.getRole())
                .build();
    }

    /**
     * 用户登出
     */
    @Override
    public void logout() {
        // 清除用户的刷新令牌
        stringRedisTemplate.opsForValue().getAndDelete(RedisEnum.USER_REFRESH_TOKEN.getKey()
                + AuthInfoContext.getLoginInfo().getId());
    }

    /**
     * 刷新访问令牌
     *
     * @param request
     * @return
     */
    @Override
    public AuthLoginVO refreshAccessToken(HttpServletRequest request) {
        // 获取cookie中的刷新令牌
        String refreshToken = Optional.ofNullable(request.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(cookie -> jwtProperties.getRefreshTokenName().equals(cookie.getName()))
                        .map(Cookie::getValue)
                        .findFirst())
                .orElseThrow(() -> new AuthException(AuthConstant.JWT_USER_INFO_EXPIRED_MESSAGE));

        // 解析刷新令牌
        Claims claims = JwtUtils.parseToken(refreshToken, jwtProperties.getSecretKey());
        if (claims == null) {
            throw new AuthException(AuthConstant.JWT_USER_INFO_EXPIRED_MESSAGE);
        }

        // 获取用户信息
        AuthLoginInfo authLoginInfo = JwtUtils.getObjectFromClaims(claims, AuthConstant.JWT_CLAIMS_USER_INFO, AuthLoginInfo.class);
        if (authLoginInfo == null) {
            throw new AuthException(AuthConstant.JWT_USER_INFO_EXPIRED_MESSAGE);
        }

        // 检查用户的刷新令牌是否与redis中的一致
        String redisRefreshToken = stringRedisTemplate.opsForValue().get(RedisEnum.USER_REFRESH_TOKEN.getKey() + authLoginInfo.getId());
        if (redisRefreshToken == null || !redisRefreshToken.equals(refreshToken)) {
            throw new AuthException(AuthConstant.JWT_USER_INFO_EXPIRED_MESSAGE);
        }

        // 生成新的访问令牌
        String newAccessToken = JwtUtils.createToken(jwtProperties.getSecretKey(),
                jwtProperties.getAccessTokenExpiration(),
                Map.of(AuthConstant.JWT_CLAIMS_USER_INFO, authLoginInfo));

        return AuthLoginVO.builder()
                .accessToken(newAccessToken)
                .id(authLoginInfo.getId())
                .accountName(authLoginInfo.getAccountName())
                .role(authLoginInfo.getRole())
                .build();

    }
}
