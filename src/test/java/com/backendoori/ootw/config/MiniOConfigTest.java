package com.backendoori.ootw.config;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;


@AutoConfigureMockMvc
@SpringBootTest
class MiniOConfigTest {

    @Autowired
    MiniOConfig miniOConfig;

    @MockBean
    MinioClient minioClient;

    @Test
    @DisplayName("bucket이 있는 경우 잘 작동한다.")
    public void configInfoCheck() throws Exception {
        //given
        when(minioClient.bucketExists(BucketExistsArgs.builder().bucket(miniOConfig.getBucket()).build())).thenReturn(
            true);

        //when
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(miniOConfig.getBucket()).build());

        //then
        assertThat(found).isTrue();
    }

    @Test
    @DisplayName("bucket이 없는 경우에도 새로 생성하고 잘 작동한다.")
    public void ifNotExistBucket() throws Exception {
        //given, when
        when(minioClient.bucketExists(BucketExistsArgs.builder()
            .bucket(miniOConfig.getBucket())
            .build()))
            .thenReturn(false);
        boolean before = minioClient.bucketExists(BucketExistsArgs.builder().bucket(miniOConfig.getBucket()).build());

        //when
        minioClient = miniOConfig.minioClient();
        boolean after = minioClient.bucketExists(BucketExistsArgs.builder()
            .bucket(miniOConfig.getBucket())
            .build());

        //then
        assertThat(before).isFalse();
        assertThat(after).isTrue();
    }

}
