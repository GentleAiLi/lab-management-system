package com.ailab.common.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 分页结果封装类
 *
 * @param <T>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private long total;
    private long pageNum;
    private long pageSize;
    private List<T> data;
}
