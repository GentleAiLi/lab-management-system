package com.ailab.common.handler;

import com.ailab.common.exception.AuthLoginException;
import com.ailab.common.exception.BaseException;
import com.ailab.common.result.ResponseResult;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Configuration
public class GlobalExceptionHandler {

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ResponseResult<Object>> handleException(Throwable e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseResult.error("服务器异常"));
    }

    @ExceptionHandler(BaseException.class)
    public ResponseResult<Object> handleBaseException(BaseException e) {
        return ResponseResult.error(e.getMessage() == null ? "业务异常" : e.getMessage());
    }

    @ExceptionHandler(AuthLoginException.class)
    public ResponseResult<Object> handleAuthLoginException(AuthLoginException e) {
        return ResponseResult.error(e.getMessage() == null ? "登录异常" : e.getMessage());
    }
}
