package com.backendoori.ootw.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.backendoori.ootw.dto.AvatarAppearanceRequest;
import com.backendoori.ootw.dto.AvatarAppearanceResponse;
import com.backendoori.ootw.repository.AvatarItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AvatarAppearanceServiceTest {

    @Autowired
    AvatarItemRepository avatarItemRepository;

    @Autowired
    AvatarAppearanceService avatarAppearanceService;

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("아바타 이미지 업로드 서비스 로직 테스트")
    public void avatarImageTest() {
        //given
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt",
            "text/plain", "some xml".getBytes());
        AvatarAppearanceRequest request = new AvatarAppearanceRequest("HAIR", true);

        //when
        AvatarAppearanceResponse avatarAppearanceResponse = avatarAppearanceService.uploadItem(file, request);

        //then
        assertThat(avatarAppearanceResponse.type()).isEqualTo(request.type());
        assertThat(avatarAppearanceResponse.sex()).isEqualTo(request.sex());
    }

}
