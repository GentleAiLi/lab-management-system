package com.ailab.common.context;

import com.ailab.pojo.dto.AuthLoginDTO;

public class AuthLoginContext {

    private static final ThreadLocal<AuthLoginDTO> loginInfo = new ThreadLocal<>();

    public static AuthLoginDTO getLoginInfo() {
        return loginInfo.get();
    }

    public static void setLoginInfo(AuthLoginDTO authLoginDTO) {
        loginInfo.set(authLoginDTO);
    }

    public static void clear() {
        loginInfo.remove();
    }
}
