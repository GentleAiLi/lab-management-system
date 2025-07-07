package com.ailab.service;

import com.ailab.pojo.dto.AuthLoginDTO;
import com.ailab.pojo.vo.AuthLoginVO;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    AuthLoginVO login(AuthLoginDTO loginInfo, HttpServletResponse response);
}
