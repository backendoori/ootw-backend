package com.backendoori.ootw.image.service;

import static com.backendoori.ootw.image.validation.ImageValidator.validateImage;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import com.backendoori.ootw.image.domain.Image;
import com.backendoori.ootw.image.dto.ImageFile;
import com.backendoori.ootw.image.exception.ImageException;
import com.backendoori.ootw.config.MiniOConfig;
import com.backendoori.ootw.image.repository.ImageRepository;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@Service
@RequiredArgsConstructor
public class MiniOImageServiceImpl implements ImageService {

    private static final int DURATION = 12;

    private final MinioClient minioClient;
    private final MiniOConfig miniOConfig;
    private final ImageRepository imageRepository;

    @Override
    public ImageFile upload(MultipartFile file) {
        validateImage(file);
        String randomFileName = getUniqueFileName(file);
        try {
            InputStream inputStream = file.getInputStream();
            String contentType = file.getContentType();
            PutObjectArgs args = PutObjectArgs.builder()
                .bucket(miniOConfig.getBucket())
                .object(randomFileName)
                .stream(inputStream, inputStream.available(), -1)
                .contentType(contentType)
                .build();
            minioClient.putObject(args);
            String url = getUrl(randomFileName);
            Image image = saveImage(url, randomFileName);
            imageRepository.save(image);
            return ImageFile.from(image);
        } catch (Exception e) {
            throw new ImageException(ImageException.IMAGE_UPLOAD_FAIL_MESSAGE);
        }
    }

    private static Image saveImage(String url, String randomFileName) {
        return Image.builder()
            .ImageUrl(url)
            .fileName(randomFileName)
            .build();
    }

    @NotNull
    private static String getUniqueFileName(MultipartFile file) {
        String randomUUID = UUID.randomUUID().toString();
        String originalFileName = file.getOriginalFilename();

        return randomUUID + originalFileName;
    }

    @Override
    public void delete(String fileName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(miniOConfig.getBucket())
                .object(fileName)
                .build());
        } catch (Exception e) {
            throw new ImageException(ImageException.IMAGE_ROLLBACK_FAIL_MESSAGE);
        }
    }

    private String getUrl(String fileName) {
        try {
            return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(miniOConfig.getBucket())
                    .object(fileName)
                    .expiry(DURATION, TimeUnit.HOURS)
                    .build());
        } catch (Exception e) {
            throw new ImageException(ImageException.IMAGE_UPLOAD_FAIL_MESSAGE);
        }
    }

}
