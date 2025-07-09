package com.ailab.common.context;

import com.ailab.common.enums.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthLoginInfo {

    private Long id;
    private String accountName;
    private UserRoleEnum role;

}
