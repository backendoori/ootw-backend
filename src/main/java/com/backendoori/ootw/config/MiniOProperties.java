package com.backendoori.ootw.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("minio")
public record MiniOProperties(
    String url,
    String accessKey,
    String secretKey,
    String bucket
) {

}
