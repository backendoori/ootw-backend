package com.backendoori.ootw.common.image;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import com.backendoori.ootw.config.MiniOConfig;
import com.backendoori.ootw.exception.ImageUploadException;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@Service
@RequiredArgsConstructor
public class MiniOImageServiceImpl implements ImageService {

    private static final int DURATION = 12;

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
            throw new ImageUploadException();
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
                    .expiry(DURATION, TimeUnit.HOURS)
                    .build());
        } catch (Exception e) {
            throw new ImageUploadException();
        }

        return url;
    }

}
