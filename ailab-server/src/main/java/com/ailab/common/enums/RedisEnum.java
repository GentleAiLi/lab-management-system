package com.ailab.common.enums;

import lombok.Getter;

import java.util.concurrent.TimeUnit;

@Getter
public enum RedisEnum {

    USER_REFRESH_TOKEN("user:refresh:token:", 7 * 24 * 60 * 60L, TimeUnit.SECONDS); // 用户刷新token

    private final String key; // Redis key前缀
    private final Long ttl; // 过期时间
    private final TimeUnit timeUnit; // 过期时间单位

    RedisEnum(String key, Long ttl, TimeUnit timeUnit) {
        this.key = key;
        this.ttl = ttl;
        this.timeUnit = timeUnit;
    }
}
