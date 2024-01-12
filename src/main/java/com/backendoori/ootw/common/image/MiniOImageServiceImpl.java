package com.backendoori.ootw.common.image;

import static com.backendoori.ootw.common.image.exception.ImageException.IMAGE_ROLLBACK_FAIL_MESSAGE;
import static com.backendoori.ootw.common.image.exception.ImageException.IMAGE_UPLOAD_FAIL_MESSAGE;
import static com.backendoori.ootw.common.validation.ImageValidator.validateImage;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import com.backendoori.ootw.common.image.exception.ImageException;
import com.backendoori.ootw.config.MiniOConfig;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
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
    public ImageFile upload(MultipartFile file) {
        validateImage(file);
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
            return new ImageFile(getUrl(), path.toString());
        } catch (Exception e) {
            throw new ImageException(IMAGE_UPLOAD_FAIL_MESSAGE);
        }
    }

    @Override
    public void delete(String fileName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(miniOConfig.getBucket())
                .object(fileName)
                .build());
        } catch (Exception e) {
            throw new ImageException(IMAGE_ROLLBACK_FAIL_MESSAGE);
        }
    }

    private String getUrl() {
        try {
            return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(miniOConfig.getBucket())
                    .object(path.toString())
                    .expiry(DURATION, TimeUnit.HOURS)
                    .build());
        } catch (Exception e) {
            throw new ImageException(IMAGE_UPLOAD_FAIL_MESSAGE);
        }
    }

}
