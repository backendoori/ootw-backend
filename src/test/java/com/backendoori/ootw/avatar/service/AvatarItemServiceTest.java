package com.backendoori.ootw.avatar.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.Stream;
import com.backendoori.ootw.avatar.dto.AvatarItemRequest;
import com.backendoori.ootw.avatar.dto.AvatarItemResponse;
import com.backendoori.ootw.avatar.repository.AvatarItemRepository;
import com.backendoori.ootw.common.image.exception.SaveException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AvatarItemServiceTest {

    private static final String VALID_TYPE = "HAIR";
    private static final String VALID_SEX = "MALE";


    @Autowired
    AvatarItemRepository avatarItemRepository;

    @Autowired
    AvatarItemService avatarItemService;

    @Autowired
    MockMvc mockMvc;

    static Stream<Arguments> provideInvalidAvatarImageInfo() {
        String validType = "HAIR";
        String validSex = "MALE";
        return java.util.stream.Stream.of(
            org.junit.jupiter.params.provider.Arguments.of(null, validSex),
            org.junit.jupiter.params.provider.Arguments.of(validType, null),
            org.junit.jupiter.params.provider.Arguments.of("", validSex),
            org.junit.jupiter.params.provider.Arguments.of(validType, ""),
            org.junit.jupiter.params.provider.Arguments.of(" ", validSex),
            org.junit.jupiter.params.provider.Arguments.of(validType, " "),
            org.junit.jupiter.params.provider.Arguments.of("hair", validSex),
            org.junit.jupiter.params.provider.Arguments.of(validType, "female")
        );
    }

    @BeforeEach
    void setup() {
        avatarItemRepository.deleteAll();
    }

    @Test
    @DisplayName("아바타 이미지 업로드에 성공한다")
    public void avatarImageTest() {
        //given
        MockMultipartFile file = new MockMultipartFile("file", "filename.jpeg",
            "image/jpeg", "some xml".getBytes());
        AvatarItemRequest request = new AvatarItemRequest(VALID_TYPE, VALID_SEX);

        //when
        AvatarItemResponse avatarItemResponse = avatarItemService.uploadItem(file, request);

        //then
        assertThat(avatarItemResponse.type()).isEqualTo(request.type());
        assertThat(avatarItemResponse.sex()).isEqualTo(request.sex());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidAvatarImageInfo")
    @DisplayName("모든 아바타 이미지 조회에 성공한다.")
    public void getListFailWithWrongRequest(String type, String sex) {
        //given , when
        MockMultipartFile file = new MockMultipartFile("file", "filename.jpeg",
            "image/jpeg", "some xml".getBytes());
        AvatarItemRequest request = new AvatarItemRequest(type, sex);

        //then
        assertThatThrownBy(() -> avatarItemService.uploadItem(file, request))
            .isInstanceOf(SaveException.class);

    }

    @Test
    @DisplayName("모든 아바타 이미지 조회에 성공한다.")
    public void getList() {
        //given , when
        for (int i = 0; i < 3; i++) {
            MockMultipartFile file = new MockMultipartFile("file", "filename.jpeg",
                "image/jpeg", "some xml".getBytes());
            AvatarItemRequest request = new AvatarItemRequest(VALID_TYPE, VALID_SEX);
            avatarItemService.uploadItem(file, request);
        }

        List<AvatarItemResponse> list = avatarItemService.getList();

        //then
        assertThat(list).hasSize(3);
        AvatarItemResponse avatarItemResponse = list.get(0);
        assertThat(avatarItemResponse).hasFieldOrPropertyWithValue("avatarItemId", avatarItemResponse.avatarItemId());
        assertThat(avatarItemResponse).hasFieldOrPropertyWithValue("type", avatarItemResponse.type());
        assertThat(avatarItemResponse).hasFieldOrPropertyWithValue("sex", avatarItemResponse.sex());
        assertThat(avatarItemResponse).hasFieldOrPropertyWithValue("url", avatarItemResponse.url());
    }

}
