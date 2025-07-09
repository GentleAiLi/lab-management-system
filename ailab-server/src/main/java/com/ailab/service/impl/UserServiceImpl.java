package com.ailab.service.impl;

import com.ailab.common.constant.ExceptionConstant;
import com.ailab.common.exception.InfoNotFoundException;
import com.ailab.mapper.UserMapper;
import com.ailab.pojo.domain.User;
import com.ailab.service.UserService;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
            throw new InfoNotFoundException(ExceptionConstant.USER_NOT_FOUND);
        }

        return user;
    }
}
