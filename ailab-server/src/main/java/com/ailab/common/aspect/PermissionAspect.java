package com.ailab.common.aspect;

import com.ailab.common.annotation.Permissions;
import com.ailab.common.constant.ExceptionConstant;
import com.ailab.common.context.AuthInfoContext;
import com.ailab.common.context.AuthLoginInfo;
import com.ailab.common.enums.UserRoleEnum;
import com.ailab.common.exception.BaseException;
import com.ailab.common.exception.InvalidParameterException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class PermissionAspect {

    @Pointcut(value = "execution(* com.ailab.controller.*.*(..)) && @annotation(com.ailab.common.annotation.Permissions)")
    public void pointCut() {
    }

    @Before("pointCut()")
    public void before(JoinPoint joinPoint) {
        log.info("权限校验");

        //获取当前方法上的用户权限类型
        MethodSignature method = (MethodSignature) joinPoint.getSignature(); //获取方法签名对象
        Permissions permissions = method.getMethod().getAnnotation(Permissions.class); //获取方法上的注解
        UserRoleEnum role = permissions.value(); //获取注解值

        //获取当前登录用户权限
        AuthLoginInfo currentUserInfo = AuthInfoContext.getLoginInfo();
        UserRoleEnum currentUserRole = currentUserInfo.getRole();
        if (currentUserRole == null) {
            log.info("用户登录信息异常，权限校验失败");
            throw new InvalidParameterException("用户登录信息异常，无法获取用户角色进行权限校验");
        }

        if (currentUserRole != role) {
            log.info("当前用户无权限");
            throw new BaseException(ExceptionConstant.PERMISSION_DENIED);
        }

        log.info("权限校验通过");
    }


}
