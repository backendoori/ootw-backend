package com.backendoori.ootw.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MiniOConfig {

    private final MiniOProperties miniOProperties;

    @Bean
    public MinioClient minioClient() throws Exception {
        MinioClient minioClient = MinioClient.builder()
            .endpoint(miniOProperties.url())
            .credentials(miniOProperties.accessKey(), miniOProperties.secretKey())
            .build();

        if (!isBucketExists(minioClient)) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(miniOProperties.bucket()).build());
        }

        return minioClient;
    }

    public String getBucket() {
        return miniOProperties.bucket();
    }

    private boolean isBucketExists(MinioClient minioClient) throws Exception {
        BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder()
            .bucket(miniOProperties.bucket())
            .build();

        return minioClient.bucketExists(bucketExistsArgs);
    }

}
