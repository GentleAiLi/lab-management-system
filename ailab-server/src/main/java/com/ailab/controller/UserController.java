package com.ailab.controller;

import com.ailab.common.enums.UserRoleEnum;
import com.ailab.common.enums.UserStatusEnum;
import com.ailab.common.result.PageResult;
import com.ailab.common.result.ResponseResult;
import com.ailab.pojo.domain.User;
import com.ailab.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseResult<User> getUserInfo(@PathVariable("id") Long id) {
        log.info("获取用户信息，用户ID：{}", id);
        User user = userService.getUserInfo(id);
        return ResponseResult.success(user);
    }

    @PostMapping
    public ResponseResult<Void> saveUser(@RequestBody User user) {
        log.info("保存用户信息：{}", user);
        userService.saveUser(user);
        return ResponseResult.success();
    }

    @PutMapping
    public ResponseResult<Void> updateUser(@RequestBody User user) {
        log.info("更新用户信息：{}", user);
        userService.updateUser(user);
        return ResponseResult.success();
    }

    @GetMapping("/page")
    public ResponseResult<PageResult<User>> getUserPage(
            @RequestParam(value = "pageNum", defaultValue = "1") Long pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Long pageSize) {
        log.info("获取用户分页信息，页码：{}，每页大小：{}", pageNum, pageSize);
        PageResult<User> result = userService.listUser(pageNum, pageSize);
        return ResponseResult.success(result);
    }

    @PutMapping("/{id}/status")
    public ResponseResult<Void> updateUserStatus(@PathVariable("id") Long id, @RequestParam("status") Integer status) {
        log.info("更新用户状态，用户ID：{}，状态：{}", id, status);
        // TODO：更新状态业务实现，以及用户相关接口的权限校验
        return null;
    }
}
