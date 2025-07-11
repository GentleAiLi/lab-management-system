package com.ailab.common.util;

import org.mindrot.jbcrypt.BCrypt;

public class BCryptUtils {

    /**
     * 对数据进行BCrypt加密
     *
     * @param data
     * @return
     */
    public static String hashData(String data) {
        return BCrypt.hashpw(data, BCrypt.gensalt());
    }

    /**
     * 验证数据是否匹配
     *
     * @param data 原始数据
     * @param hashData BCrypt加密后的数据
     * @return
     */
    public static boolean checkData(String data, String hashData) {
        return BCrypt.checkpw(data, hashData);
    }
}
