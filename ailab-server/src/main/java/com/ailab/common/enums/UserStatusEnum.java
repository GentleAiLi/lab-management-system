package com.ailab.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum UserStatusEnum {

    DISABLED(0),
    ENABLED(1);

    @EnumValue
    @JsonValue
    private final int value;

    UserStatusEnum(int value) {
        this.value = value;
    }
}
