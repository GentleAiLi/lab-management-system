package com.ailab.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ailab.aes")
@Data
public class AesProperties {

    private String key; // AES加密密钥
}
