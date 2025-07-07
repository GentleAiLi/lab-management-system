package com.ailab.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum UserRoleEnum {

    USER(0),
    ADMIN(1);

    @EnumValue
    @JsonValue
    private final int value;

    UserRoleEnum(int value) {
        this.value = value;
    }

}
