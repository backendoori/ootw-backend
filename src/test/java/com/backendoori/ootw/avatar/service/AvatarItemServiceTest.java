package com.backendoori.ootw.avatar.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.Stream;
import com.backendoori.ootw.avatar.domain.ItemType;
import com.backendoori.ootw.avatar.domain.Sex;
import com.backendoori.ootw.avatar.dto.AvatarItemRequest;
import com.backendoori.ootw.avatar.dto.AvatarItemResponse;
import com.backendoori.ootw.avatar.repository.AvatarItemRepository;
import com.backendoori.ootw.image.exception.SaveException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
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

    @BeforeEach
    void setup() {
        avatarItemRepository.deleteAll();
    }

    @Nested
    @DisplayName("아바타 아이템 업로드")
    class AvatarUpload {

        @ParameterizedTest(name = "[{index}]: 아이템 타입이 {0}인 경우에 저장에 성공한다.")
        @ValueSource(strings = {"image/jpeg", "image/gif", "image/png", "image/jpg"})
        @DisplayName("여러 이미지 타입에 대해 업로드에 성공한다")
        public void avatarImageTest(String contentType) {
            //given
            MockMultipartFile file = new MockMultipartFile("file", "filename.jpeg",
                contentType, "some xml".getBytes());
            AvatarItemRequest request = new AvatarItemRequest(VALID_TYPE, VALID_SEX);

            //when
            AvatarItemResponse avatarItemResponse = avatarItemService.upload(file, request);

            //then
            assertThat(avatarItemResponse.type()).isEqualTo(request.type());
            assertThat(avatarItemResponse.sex()).isEqualTo(request.sex());
        }

        @ParameterizedTest(name = "[{index}] 아이템 타입이 {0}, 성별이 {1}인 경우 예외가 발생한다.")
        @MethodSource("provideInvalidAvatarImageInfo")
        @DisplayName("잘못된 이이템 타입이나 성별을 기입할 시 예외가 발생한다.")
        public void uploadFailWithWrongRequest(String type, String sex) {
            //given , when
            MockMultipartFile file = new MockMultipartFile("file", "filename.jpeg",
                "image/jpeg", "some xml".getBytes());
            AvatarItemRequest request = new AvatarItemRequest(type, sex);

            //then
            assertThatThrownBy(() -> avatarItemService.upload(file, request))
                .isInstanceOf(SaveException.class);

        }

        @ParameterizedTest(name = "[{index}] content-type 이 {0}인 경우 예외가 발생한다.")
        @ValueSource(strings = {"text/plain", "application/json", "xml"})
        @DisplayName("파일의 유형이 이미지가 아닌 경우 예외가 발생한다.")
        public void imageUpLoadWithInvalidContentType(String contentType) throws Exception {
            //given
            MockMultipartFile file = new MockMultipartFile("file", "filename.txt",
                contentType, "some xml".getBytes());
            AvatarItemRequest request = new AvatarItemRequest(ItemType.TOP.name(), Sex.MALE.name());

            //when, then
            assertThatThrownBy(() -> avatarItemService.upload(file, request))
                .isInstanceOf(IllegalArgumentException.class);
        }

        static Stream<Arguments> provideInvalidAvatarImageInfo() {
            String validType = "HAIR";
            String validSex = "MALE";
            return java.util.stream.Stream.of(
                Arguments.of(null, validSex),
                Arguments.of(validType, null),
                Arguments.of("", validSex),
                Arguments.of(validType, ""),
                Arguments.of(" ", validSex),
                Arguments.of(validType, " "),
                Arguments.of("hair", validSex),
                Arguments.of(validType, "female")
            );
        }

    }

    @Test
    @DisplayName("모든 아바타 이미지 조회에 성공한다.")
    public void getList() {
        //given , when
        for (int i = 0; i < 3; i++) {
            MockMultipartFile file = new MockMultipartFile("file", "filename.jpeg",
                "image/jpeg", "some xml".getBytes());
            AvatarItemRequest request = new AvatarItemRequest(VALID_TYPE, VALID_SEX);
            avatarItemService.upload(file, request);
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
