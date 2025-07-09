package com.ailab.common.context;

public class AuthInfoContext {

    private static final ThreadLocal<AuthLoginInfo> loginInfo = new ThreadLocal<>();

    public static AuthLoginInfo getLoginInfo() {
        return loginInfo.get();
    }

    public static void setLoginInfo(AuthLoginInfo authLoginInfo) {
        loginInfo.set(authLoginInfo);
    }

    public static void clear() {
        loginInfo.remove();
    }
}
