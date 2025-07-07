package com.ailab.service.impl;

import com.ailab.common.constant.AuthTokenConstant;
import com.ailab.common.exception.AuthLoginException;
import com.ailab.common.exception.BaseException;
import com.ailab.common.exception.InfoNotFoundException;
import com.ailab.common.properties.JwtProperties;
import com.ailab.common.util.JwtUtils;
import com.ailab.mapper.UserMapper;
import com.ailab.pojo.domain.User;
import com.ailab.pojo.dto.AuthLoginDTO;
import com.ailab.pojo.vo.AuthLoginVO;
import com.ailab.service.UserService;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;

    /**
     * 获取用户详细信息
     *
     * @param id
     * @return
     */
    @Override
    public User getUserInfo(Long id) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<>(User.class)
                .select(User::getId, User::getAccountName, User::getName,
                        User::getRole, User::getSno, User::getStatus, User::getCreateTime)
                .eq(User::getId, id));

        if (user == null) {
            throw new InfoNotFoundException("用户信息不存在");
        }

        return user;
    }
}
