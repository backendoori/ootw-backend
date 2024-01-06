package com.backendoori.ootw.common.image;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import com.backendoori.ootw.config.MiniOConfig;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@Controller
@RequiredArgsConstructor
public class MiniOImageServiceImpl implements ImageService {

    private final MinioClient minioClient;
    private final MiniOConfig miniOConfig;
    private Path path;

    @Override
    public String uploadImage(MultipartFile file) {
        try {
            path = Path.of(file.getOriginalFilename());
            InputStream inputStream = file.getInputStream();
            String contentType = file.getContentType();
            PutObjectArgs args = PutObjectArgs.builder()
                .bucket(miniOConfig.getBucket())
                .object(path.toString())
                .stream(inputStream, inputStream.available(), -1)
                .contentType(contentType)
                .build();
            minioClient.putObject(args);
        } catch (Exception e) {
            log.warn("Exception occurred while saving contents : {}", e.getMessage(), e);
        }

        return getUrl();
    }

    private String getUrl() {
        String url = null;
        try {
            url = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(miniOConfig.getBucket())
                    .object(path.toString())
                    .expiry(12, TimeUnit.HOURS)
                    .build());
        } catch (Exception e) {
            log.warn("Exception Occurred while getting: {}", e.getMessage(), e);
        }

        return url;
    }

}
