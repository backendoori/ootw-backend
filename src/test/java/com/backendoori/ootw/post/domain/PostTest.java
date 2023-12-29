package com.backendoori.ootw.post.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.util.stream.Stream;
import com.backendoori.ootw.post.domain.weather.Weather;
import com.backendoori.ootw.post.dto.PostSaveRequest;
import com.backendoori.ootw.post.dto.WeatherDto;
import com.backendoori.ootw.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PostTest {

    private static final Long USER_ID = 1L;
    private static final WeatherDto WEATHER_DTO = new WeatherDto(0.0, -10.0, 10.0, 1, 1);
    private static final WeatherDto INVALID_WEATHER_DTO = new WeatherDto(-900.0, -10.0, 10.0, 1, 1);
    private static final User MOCK_USER = mock(User.class);

    private static Stream<Arguments> provideInvalidInfo() {
        return Stream.of(
            Arguments.of("userId가 null인 경우",
                new PostSaveRequest(null, "Test Title", "Test Content", null, WEATHER_DTO)),
            Arguments.of("title이 null인 경우",
                new PostSaveRequest(USER_ID, null, "Test Content", null, WEATHER_DTO)),
            Arguments.of("title이 공백인 경우",
                new PostSaveRequest(USER_ID, " ", "Test Content", null, WEATHER_DTO)),
            Arguments.of("title이 30자를 넘는 경우",
                new PostSaveRequest(USER_ID, "T".repeat(31), "Test Content", null, WEATHER_DTO)),
            Arguments.of("content가 null인 경우",
                new PostSaveRequest(USER_ID, "Test Title", null, null, WEATHER_DTO)),
            Arguments.of("content가 공백인 경우",
                new PostSaveRequest(USER_ID, "Test Title", " ", null, WEATHER_DTO)),
            Arguments.of("content가 500자를 넘는 경우",
                new PostSaveRequest(USER_ID, "Test Title", "T".repeat(501), null, WEATHER_DTO)),
            Arguments.of("weather가 null인 경우",
                new PostSaveRequest(USER_ID, "Test Title", "Test Content", null, null)),
            Arguments.of("weather가 유효하지 않은 값인 경우",
                new PostSaveRequest(USER_ID, "Test Title", "Test Content", null, INVALID_WEATHER_DTO))
        );
    }

    @Test
    @DisplayName("from 메서드로 유효한 User, PostSaveRequest로부터 Post를 생성하는 것에 성공한다.")
    void createPostSuccess() {
        // given
        PostSaveRequest request =
            new PostSaveRequest(USER_ID, "Test Title", "Test Content", null, WEATHER_DTO);

        // when
        Post createdPost = Post.from(MOCK_USER, request);

        // then
        assertAll(
            () -> assertThat(createdPost).hasFieldOrPropertyWithValue("user", MOCK_USER),
            () -> assertThat(createdPost).hasFieldOrPropertyWithValue("title", request.title()),
            () -> assertThat(createdPost).hasFieldOrPropertyWithValue("content", request.content()),
            () -> assertThat(createdPost).hasFieldOrPropertyWithValue("image", request.image()),
            () -> assertThat(createdPost).hasFieldOrPropertyWithValue("weather", Weather.from(WEATHER_DTO))
        );
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("provideInvalidInfo")
    @DisplayName("from 메서드로 유효하지 않은 User, PostSaveRequest로부터 Post를 생성하는 것에 실패한다.")
    void createPostFail(String info, PostSaveRequest postSaveRequest) {
        // given, when, then
        assertThrows(IllegalArgumentException.class,
            () -> Post.from(MOCK_USER, postSaveRequest));

        // TODO: 에러 메시지 검증
    }

}
