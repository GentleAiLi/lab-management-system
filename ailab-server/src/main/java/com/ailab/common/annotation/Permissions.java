package com.ailab.common.annotation;

import com.ailab.common.enums.UserRoleEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Permissions {

    /**
     * 权限类型
     *
     * @return
     */
    UserRoleEnum value();
}
