package com.backendoori.ootw.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import io.minio.MinioClient;
import io.minio.RemoveBucketArgs;
import io.minio.messages.Bucket;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class MiniOConfigTest {

    static String testBucketName = "temporarytestbucket-1";

    @Autowired
    MiniOProperties miniOProperties;

    MiniOConfig miniOConfig;
    MinioClient minioClient;

    @BeforeAll
    void setup() throws Exception {
        MiniOProperties emptyBucketProperties = new MiniOProperties(
            miniOProperties.url(),
            miniOProperties.accessKey(),
            miniOProperties.secretKey(),
            testBucketName
        );

        miniOConfig = new MiniOConfig(emptyBucketProperties);
        minioClient = miniOConfig.minioClient();

        removeTestBucket();
    }

    @AfterAll
    void cleanup() throws Exception {
        removeTestBucket();
    }

    @Test
    @Order(1)
    @DisplayName("bucket이 없을 경우 새로 생성한다.")
    public void testReturnNewBucket() throws Exception {
        // given
        List<String> before = getBucketNames();

        // when
        miniOConfig.minioClient();

        // then
        List<String> after = getBucketNames();

        assertThat(after).hasSize(before.size() + 1);
    }

    @Test
    @Order(2)
    @DisplayName("bucket이 존재할 경우 기존 버킷을 반환한다")
    public void testReturnExistBucket() throws Exception {
        // given
        List<String> before = getBucketNames();

        // when
        miniOConfig.minioClient();

        // then
        List<String> after = getBucketNames();

        assertThat(after).hasSize(before.size());
        assertThat(after).containsExactlyElementsOf(before);
    }

    private List<String> getBucketNames() throws Exception {
        return minioClient.listBuckets()
            .stream()
            .map(Bucket::name)
            .toList();
    }

    private void removeTestBucket() throws Exception {
        RemoveBucketArgs removeBucketArgs = RemoveBucketArgs.builder()
            .bucket(testBucketName)
            .build();

        minioClient.removeBucket(removeBucketArgs);
    }

}
