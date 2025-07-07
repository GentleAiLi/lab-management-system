package com.ailab.controller;

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
    public ResponseResult<User> getUserInfo(@PathVariable Long id) {
        log.info("获取用户信息，用户ID：{}", id);
        User user = userService.getUserInfo(id);
        return ResponseResult.success(user);
    }
}
