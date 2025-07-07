package com.ailab.service;

import com.ailab.pojo.domain.User;
import com.ailab.pojo.dto.AuthLoginDTO;
import com.ailab.pojo.vo.AuthLoginVO;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService extends IService<User> {

    User getUserInfo(Long id);
}
