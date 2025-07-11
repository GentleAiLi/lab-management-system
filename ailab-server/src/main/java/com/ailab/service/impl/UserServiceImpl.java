package com.ailab.service.impl;

import com.ailab.common.constant.ExceptionConstant;
import com.ailab.common.exception.BaseException;
import com.ailab.common.exception.InfoNotFoundException;
import com.ailab.common.exception.InvalidParameterException;
import com.ailab.common.properties.AesProperties;
import com.ailab.common.result.PageResult;
import com.ailab.common.util.AesUtils;
import com.ailab.common.util.BCryptUtils;
import com.ailab.common.util.RegexUtils;
import com.ailab.mapper.UserMapper;
import com.ailab.pojo.domain.User;
import com.ailab.service.UserService;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final AesProperties aesProperties;

    /**
     * 获取用户详细信息
     *
     * @param id
     * @return
     */
    @Override
    public User getUserInfo(Long id) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<>(User.class)
                .select(User::getId, User::getAccountName, User::getName, User::getEmail,
                        User::getRole, User::getSno, User::getStatus, User::getCreateTime)
                .eq(User::getId, id));

        if (user == null) {
            throw new InfoNotFoundException(ExceptionConstant.USER_NOT_FOUND);
        }

        // 对邮箱进行脱敏处理
        String email = user.getEmail();
        if (email != null) {
            // 对邮箱进行格式校验
            user.setEmail(AesUtils.decrypt(email, aesProperties.getKey()));
        }

        return user;
    }

    /**
     * 保存用户信息
     *
     * @param user
     */
    @Override
    public void saveUser(User user) {
        // 对密码进行格式校验
        if (RegexUtils.isInvalidPassword(user.getPassword())) {
            throw new InvalidParameterException(ExceptionConstant.INVALID_PASSWORD);
        }
        // 对密码进行加密
        user.setPassword(BCryptUtils.hashData(user.getPassword()));

        // 对邮箱进行加密
        if (user.getEmail() != null) {
            // 对邮箱进行格式校验
            if (RegexUtils.isInvalidEmail(user.getEmail())) {
                throw new InvalidParameterException(ExceptionConstant.INVALID_EMAIL);
            }
            user.setEmail(AesUtils.encrypt(user.getEmail(), aesProperties.getKey()));
        }

        // 保存用户信息
        int insert = userMapper.insert(user);
        if (insert <= 0) {
            throw new BaseException(ExceptionConstant.USER_SAVE_FAILED);
        }
    }

    /**
     * 更新用户信息
     *
     * @param user
     */
    @Override
    public void updateUser(User user) {
        // 对密码进行加密
        if (user.getPassword() != null) {
            // 对密码进行格式校验
            if (RegexUtils.isInvalidPassword(user.getPassword())) {
                throw new InvalidParameterException(ExceptionConstant.INVALID_PASSWORD);
            }
            user.setPassword(BCryptUtils.hashData(user.getPassword()));
        }

        // 对邮箱进行加密
        if (user.getEmail() != null) {
            // 对邮箱进行格式校验
            if (RegexUtils.isInvalidEmail(user.getEmail())) {
                throw new InvalidParameterException(ExceptionConstant.INVALID_EMAIL);
            }
            user.setEmail(AesUtils.encrypt(user.getEmail(), aesProperties.getKey()));
        }

        // 更新用户信息
        int update = userMapper.updateById(user);
        if (update <= 0) {
            throw new BaseException(ExceptionConstant.USER_UPDATE_FAILED);
        }
    }

    /**
     * 分页查询用户列表
     *
     * @param pageNum
     * @param pageSize
     */
    @Override
    public PageResult<User> listUser(long pageNum, long pageSize) {
        // 分页查询用户列表
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(User::getId, User::getAccountName, User::getRole, User::getStatus, User::getCreateTime)
                .orderByDesc(User::getCreateTime, User::getAccountName);

        Page<User> userPage = userMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);

        return new PageResult<>(userPage.getTotal(), pageNum, pageSize, userPage.getRecords());
    }

}
