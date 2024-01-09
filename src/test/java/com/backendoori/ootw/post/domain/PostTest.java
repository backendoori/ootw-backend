package com.backendoori.ootw.post.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.util.stream.Stream;
import com.backendoori.ootw.post.dto.PostSaveRequest;
import com.backendoori.ootw.user.domain.User;
import com.backendoori.ootw.weather.domain.TemperatureArrange;
import com.backendoori.ootw.weather.dto.TemperatureArrangeDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PostTest {

    private static final int NX = 55;
    private static final int NY = 127;
    private static final String IMG_URL = "imgUrl";
    private static final User MOCK_USER = mock(User.class);
    private static final TemperatureArrangeDto TEMPERATURE_ARRANGE_DTO = new TemperatureArrangeDto(-10.0, 10.0);
    private static final TemperatureArrange TEMPERATURE_ARRANGE = TemperatureArrange.from(TEMPERATURE_ARRANGE_DTO);

    private static Stream<Arguments> provideInvalidInfo() {
        return Stream.of(Arguments.of("title이 null인 경우",
                new PostSaveRequest(null, "Test Content", NX, NY), TEMPERATURE_ARRANGE),
            Arguments.of("title이 공백인 경우",
                new PostSaveRequest(" ", "Test Content", NX, NY), TEMPERATURE_ARRANGE),
            Arguments.of("title이 30자를 넘는 경우",
                new PostSaveRequest("T".repeat(31), "Test Content", NX, NY), TEMPERATURE_ARRANGE),
            Arguments.of("content가 null인 경우",
                new PostSaveRequest("Test Title", null, NX, NY), TEMPERATURE_ARRANGE),
            Arguments.of("content가 공백인 경우",
                new PostSaveRequest("Test Title", " ", NX, NY), TEMPERATURE_ARRANGE),
            Arguments.of("content가 500자를 넘는 경우",
                new PostSaveRequest("Test Title", "T".repeat(501), NX, NY), TEMPERATURE_ARRANGE),
            Arguments.of("temperatureArrange가 null인 경우",
                new PostSaveRequest("Test Title", "T".repeat(501), NX, NY), null)
        );
    }

    @Test
    @DisplayName("PostSaveRequest로부터 Post를 생성하는 것에 성공한다.")
    void createPostSuccess() {
        // given
        PostSaveRequest request = new PostSaveRequest("Test Title", "Test Content", NX, NY);

        // when
        Post createdPost = Post.from(MOCK_USER, request, IMG_URL, TEMPERATURE_ARRANGE);

        // then
        assertAll(() -> assertThat(createdPost).hasFieldOrPropertyWithValue("user", MOCK_USER),
            () -> assertThat(createdPost).hasFieldOrPropertyWithValue("title", request.title()),
            () -> assertThat(createdPost).hasFieldOrPropertyWithValue("content", request.content()),
            () -> assertThat(createdPost).hasFieldOrPropertyWithValue("image", IMG_URL),
            () -> assertThat(createdPost).hasFieldOrPropertyWithValue("temperatureArrange", TemperatureArrange.from(
                TEMPERATURE_ARRANGE_DTO)));
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("provideInvalidInfo")
    @DisplayName("from 메서드로 유효하지 않은 User, PostSaveRequest로부터 Post를 생성하는 것에 실패한다.")
    void createPostFail(String info, PostSaveRequest postSaveRequest, TemperatureArrange temperatureArrange) {
        // given // when, then
        assertThrows(IllegalArgumentException.class,
            () -> Post.from(MOCK_USER, postSaveRequest, IMG_URL, temperatureArrange));
    }

}
