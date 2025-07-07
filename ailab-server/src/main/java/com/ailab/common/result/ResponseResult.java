package com.ailab.common.result;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 通用响应结果类
 *
 * @param <T>
 */
@Data
public class ResponseResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer code; // 0表示失败，1表示成功
    private String message; // 响应消息
    private T data; // 响应数据

    public static <T> ResponseResult<T> success() {
        ResponseResult<T> result = new ResponseResult<>();
        result.setCode(1);
        return result;
    }

    public static <T> ResponseResult<T> success(T data) {
        ResponseResult<T> result = success();
        result.setData(data);
        return result;
    }

    public static <T> ResponseResult<T> error(String message) {
        ResponseResult<T> result = new ResponseResult<>();
        result.setCode(0);
        result.setMessage(message);
        return result;
    }
}
