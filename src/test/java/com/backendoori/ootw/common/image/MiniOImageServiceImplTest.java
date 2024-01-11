package com.backendoori.ootw.common.image;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;
import com.backendoori.ootw.common.image.exception.ImageException;
import com.backendoori.ootw.config.MiniOConfig;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@AutoConfigureMockMvc
class MiniOImageServiceImplTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ImageService imageService;

    @Mock
    private MinioClient minioClient;

    @Mock
    private MiniOConfig miniOConfig;

    @InjectMocks
    private MiniOImageServiceImpl mockingImageService;

    static Stream<Arguments> provideInvalidImageInfo() {
        String validContentType = "image/jpeg";
        byte[] validContent = "content".getBytes();
        return java.util.stream.Stream.of(
            org.junit.jupiter.params.provider.Arguments.of("xml", validContent),
            org.junit.jupiter.params.provider.Arguments.of("pdf", validContent),
            org.junit.jupiter.params.provider.Arguments.of(validContentType, "".getBytes()),
            org.junit.jupiter.params.provider.Arguments.of(" ", validContent),
            org.junit.jupiter.params.provider.Arguments.of("", validContent),
            org.junit.jupiter.params.provider.Arguments.of(validContentType, null)
        );
    }

    @Test
    @DisplayName("이미지 업로드에 성공한다.")
    public void imageUpload() {
        //given
        MockMultipartFile file = new MockMultipartFile("file", "filename.jpeg",
            "image/jpeg", "some xml".getBytes());
        //when, then
        assertThatCode(() -> imageService.uploadImage(file))
            .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("아바타 이미지 업로드 서비스 로직 테스트")
    public void imageUploadFailWithNullImage(MockMultipartFile file) {
        //given, when, then
        assertThatCode(() -> imageService.uploadImage(file))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidImageInfo")
    @DisplayName("아바타 이미지 업로드 서비스 로직 테스트")
    public void imageUploadFailWithInvalidImage(String contentType, byte[] content) {
        //given
        MockMultipartFile file = new MockMultipartFile("file", "filename.jpeg",
            contentType, content);
        //when, then
        assertThatCode(() -> imageService.uploadImage(file))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("이미지 업로드 중 예외가 발생하면 커스텀 예외가 발생한다.")
    void exceptionWhileUploadingImage() throws Exception {
        // Given
        MultipartFile file =
            new MockMultipartFile("file", "testfile.jpeg", "image/jpeg", "test image content".getBytes());
        doThrow(new RuntimeException("Mock Exception")).when(minioClient).putObject(any(PutObjectArgs.class));

        // When, Then
        when(miniOConfig.getBucket()).thenReturn("test-bucket");

        assertThatThrownBy(() -> mockingImageService.uploadImage(file))
            .isInstanceOf(ImageException.class);
    }

    @Test
    void exceptionWhileDeletingImage() throws Exception {
        String fileName = "testfile.jpeg";
        doThrow(new RuntimeException("Mock Exception")).when(minioClient).removeObject(any(RemoveObjectArgs.class));

        assertThatThrownBy(() -> mockingImageService.deleteImage(fileName))
            .isInstanceOf(ImageException.class);
    }

    @Test
    void exceptionWhileGettingUrl() throws Exception {
        MultipartFile file =
            new MockMultipartFile("file", "testfile.jpeg", "image/jpeg", "test image content".getBytes());
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class))).thenThrow(
            new RuntimeException("Mock Exception"));

        assertThatThrownBy(() -> mockingImageService.uploadImage(file))
            .isInstanceOf(ImageException.class);

    }


}
