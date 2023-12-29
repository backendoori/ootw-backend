package com.backendoori.ootw.common.image;

import static org.assertj.core.api.Assertions.assertThatCode;

import com.backendoori.ootw.common.image.ImageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class MiniOImageServiceImplTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ImageService imageService;

    @Test
    @DisplayName("아바타 이미지 업로드 서비스 로직 테스트")
    public void imageUploadTest(){
        //given
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt",
            "text/plain", "some xml".getBytes());
        //when, then
        assertThatCode(() -> imageService.uploadImage(file))
            .doesNotThrowAnyException();
    }
}
