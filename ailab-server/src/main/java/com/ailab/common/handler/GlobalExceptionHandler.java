package com.ailab.common.handler;

import com.ailab.common.exception.AuthException;
import com.ailab.common.exception.BaseException;
import com.ailab.common.exception.InfoNotFoundException;
import com.ailab.common.result.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ResponseResult<Object>> handleException(Throwable e) {
        log.error("全局异常处理: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseResult.error("服务器异常"));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ResponseResult<Object>> handleNoResourceFoundException(NoResourceFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseResult.error("资源不存在"));
    }

    @ExceptionHandler(InfoNotFoundException.class)
    public ResponseResult<Object> handleInfoNotFoundException(InfoNotFoundException e) {
        log.error("信息未找到异常: {}", e.getMessage(), e);
        return ResponseResult.error(e.getMessage() == null ? "信息未找到" : e.getMessage());
    }

    @ExceptionHandler(BaseException.class)
    public ResponseResult<Object> handleBaseException(BaseException e) {
        log.error("业务异常: {}", e.getMessage(), e);
        return ResponseResult.error(e.getMessage() == null ? "业务异常" : e.getMessage());
    }

    @ExceptionHandler(AuthException.class)
    public ResponseResult<Object> handleAuthLoginException(AuthException e) {
        log.error("权限异常: {}", e.getMessage(), e);
        return ResponseResult.error(e.getMessage() == null ? "权限异常" : e.getMessage());
    }
}
