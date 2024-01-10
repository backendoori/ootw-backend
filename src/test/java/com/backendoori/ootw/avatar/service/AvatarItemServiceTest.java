package com.backendoori.ootw.avatar.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.backendoori.ootw.avatar.domain.Sex;
import com.backendoori.ootw.avatar.dto.AvatarItemRequest;
import com.backendoori.ootw.avatar.dto.AvatarItemResponse;
import com.backendoori.ootw.avatar.repository.AvatarItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AvatarItemServiceTest {

    @Autowired
    AvatarItemRepository avatarItemRepository;

    @Autowired
    AvatarItemService avatarItemService;

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("아바타 이미지 업로드 서비스 로직 테스트")
    public void avatarImageTest() {
        //given
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt",
            "text/plain", "some xml".getBytes());
        AvatarItemRequest request = new AvatarItemRequest("HAIR", Sex.MALE.name());

        //when
        AvatarItemResponse avatarItemResponse = avatarItemService.uploadItem(file, request);

        //then
        assertThat(avatarItemResponse.type()).isEqualTo(request.type());
        assertThat(avatarItemResponse.sex()).isEqualTo(request.sex());
    }

}
