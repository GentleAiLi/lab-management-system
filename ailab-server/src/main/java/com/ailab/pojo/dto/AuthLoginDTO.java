package com.ailab.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthLoginDTO {

    private String accountName;
    private String password;

    @Override
    public String toString() {
        return "AuthLoginVO{" +
                "accountName='" + accountName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
