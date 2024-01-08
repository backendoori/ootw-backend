package com.backendoori.ootw.post.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.util.stream.Stream;
import com.backendoori.ootw.post.dto.PostSaveRequest;
import com.backendoori.ootw.user.domain.User;
import com.backendoori.ootw.weather.domain.Weather;
import com.backendoori.ootw.weather.dto.WeatherDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PostTest {

    private static final WeatherDto WEATHER_DTO = new WeatherDto(-10.0, 10.0);
    private static final WeatherDto INVALID_WEATHER_DTO = new WeatherDto(-900.0, 10.0);
    private static final User MOCK_USER = mock(User.class);

    private static Stream<Arguments> provideInvalidInfo() {
        return Stream.of(Arguments.of("title이 null인 경우",
                new PostSaveRequest(null, "Test Content", WEATHER_DTO)),
            Arguments.of("title이 공백인 경우",
                new PostSaveRequest(" ", "Test Content", WEATHER_DTO)),
            Arguments.of("title이 30자를 넘는 경우",
                new PostSaveRequest("T".repeat(31), "Test Content", WEATHER_DTO)),
            Arguments.of("content가 null인 경우",
                new PostSaveRequest("Test Title", null, WEATHER_DTO)),
            Arguments.of("content가 공백인 경우",
                new PostSaveRequest("Test Title", " ", WEATHER_DTO)),
            Arguments.of("content가 500자를 넘는 경우",
                new PostSaveRequest("Test Title", "T".repeat(501), WEATHER_DTO)),
            Arguments.of("weather가 null인 경우",
                new PostSaveRequest("Test Title", "Test Content", null)),
            Arguments.of("weather가 유효하지 않은 값인 경우",
                new PostSaveRequest("Test Title", "Test Content", INVALID_WEATHER_DTO)));
    }

    @Test
    @DisplayName("PostSaveRequest로부터 Post를 생성하는 것에 성공한다.")
    void createPostSuccess() {
        // given
        PostSaveRequest request = new PostSaveRequest("Test Title", "Test Content", WEATHER_DTO);
        String imgUrl = "imgUrl";

        // when
        Post createdPost = Post.from(MOCK_USER, request, imgUrl);

        // then
        assertAll(() -> assertThat(createdPost).hasFieldOrPropertyWithValue("user", MOCK_USER),
            () -> assertThat(createdPost).hasFieldOrPropertyWithValue("title", request.title()),
            () -> assertThat(createdPost).hasFieldOrPropertyWithValue("content", request.content()),
            () -> assertThat(createdPost).hasFieldOrPropertyWithValue("image", imgUrl),
            () -> assertThat(createdPost).hasFieldOrPropertyWithValue("weather", Weather.from(WEATHER_DTO)));
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("provideInvalidInfo")
    @DisplayName("from 메서드로 유효하지 않은 User, PostSaveRequest로부터 Post를 생성하는 것에 실패한다.")
    void createPostFail(String info, PostSaveRequest postSaveRequest) {
        // given, when, then
        String imgUrl = "imgUrl";

        assertThrows(IllegalArgumentException.class, () -> Post.from(MOCK_USER, postSaveRequest, imgUrl));

        // TODO: 에러 메시지 검증
    }

}
