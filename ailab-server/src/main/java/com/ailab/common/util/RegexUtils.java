package com.ailab.common.util;

public class RegexUtils {

    // 邮箱正则
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    // 密码正则，8-16位，至少包含一个字母和一个数字，可以包含特殊字符
    private static final String PASSWORD_REGEX = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?.]{8,16}$";

    /**
     * 验证邮箱格式是否正确
     *
     * @param email
     * @return
     */
    public static boolean isInvalidEmail(String email) {
        return email != null && email.matches(EMAIL_REGEX);
    }

    /**
     * 验证密码格式是否正确
     * 密码必须为8-16位，至少包含一个字母和一个数字，可以包含特殊字符
     *
     * @param password
     * @return
     */
    public static boolean isInvalidPassword(String password) {
        return password != null && password.matches(PASSWORD_REGEX);
    }
}
