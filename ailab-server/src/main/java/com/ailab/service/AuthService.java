package com.ailab.service;

import com.ailab.pojo.dto.AuthLoginDTO;
import com.ailab.pojo.vo.AuthLoginVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    AuthLoginVO login(AuthLoginDTO loginInfo, HttpServletResponse response);

    void logout();

    AuthLoginVO refreshAccessToken(HttpServletRequest request);
}
