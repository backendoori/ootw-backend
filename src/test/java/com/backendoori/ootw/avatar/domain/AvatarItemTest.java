package com.backendoori.ootw.avatar.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;
import com.backendoori.ootw.avatar.dto.AvatarItemRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class AvatarItemTest {

    @Test
    @DisplayName("아바타 옷 생성에 성공한다.")
    public void createTest() {
        //given
        AvatarItemRequest request = new AvatarItemRequest("HAIR", Sex.MALE.name());
        String url = "url";

        //when
        AvatarItem avatarItem = AvatarItem.create(request, url);


        //then
        assertThat(request.type()).isEqualTo(avatarItem.getItemType().name());
        assertThat(request.sex()).isEqualTo(avatarItem.getSex().name());
    }

    @ParameterizedTest(name = "[{index}] 아이템 타입이 {0}이고 성별이 {1}이며, 생성된 이미지의 url이 {2} 경우")
    @MethodSource("provideInvalidAvatarImageInfo")
    @DisplayName("적절하지 않은 값이 포함되면 아바타 아이템 생성에 실패한다.")
    public void createTestFailWithInvalidSource(String type, String sex, String url) {
        //given
        AvatarItemRequest request = new AvatarItemRequest(type, sex);

        //when, then
        assertThatThrownBy(() -> AvatarItem.create(request, url))
            .isInstanceOf(IllegalArgumentException.class);

    }

    static Stream<Arguments> provideInvalidAvatarImageInfo() {
        String validType = "HAIR";
        String validSex = "MALE";
        String validImage = "imageUrl";
        return Stream.of(
            Arguments.of(null, validSex, validImage),
            Arguments.of(validType, null, validImage),
            Arguments.of("", validSex, validImage),
            Arguments.of(validType, "", validImage),
            Arguments.of(" ", validSex, validImage),
            Arguments.of(validType, " ", validImage),
            Arguments.of("a".repeat(40), validSex, validImage),
            Arguments.of(validType, "a".repeat(600), validImage),
            Arguments.of(validType, validSex, null),
            Arguments.of(validType, validSex, ""),
            Arguments.of(validType, validSex, "  ")
        );
    }

}
