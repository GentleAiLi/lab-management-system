package com.ailab.pojo.vo;

import com.ailab.common.enums.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthLoginVO {
    private Long id;
    private String accountName; // 账户名
    private UserRoleEnum role; // 身份权限，1管理员，0普通用户
    private String accessToken; // 访问令牌
}
