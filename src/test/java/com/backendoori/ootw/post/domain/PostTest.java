package com.backendoori.ootw.post.domain;

import static com.backendoori.ootw.post.validation.Message.BLANK_POST_CONTENT;
import static com.backendoori.ootw.post.validation.Message.BLANK_POST_TITLE;
import static com.backendoori.ootw.post.validation.Message.INVALID_POST_CONTENT;
import static com.backendoori.ootw.post.validation.Message.INVALID_POST_TITLE;
import static com.backendoori.ootw.post.validation.Message.NULL_TEMPERATURE_ARRANGE;
import static com.backendoori.ootw.post.validation.Message.NULL_WRITER;
import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.VALID_COORDINATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import com.backendoori.ootw.post.dto.PostSaveRequest;
import com.backendoori.ootw.user.domain.User;
import com.backendoori.ootw.weather.domain.TemperatureArrange;
import com.backendoori.ootw.weather.domain.forecast.ForecastCategory;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PostTest {

    private static final String IMG_URL = "imgUrl";
    private static final User MOCK_USER = mock(User.class);

    private static TemperatureArrange generateTemperatureArrange() {
        Map<ForecastCategory, String> weatherInfoMap = new HashMap<>();
        weatherInfoMap.put(ForecastCategory.TMN, String.valueOf(0.0));
        weatherInfoMap.put(ForecastCategory.TMX, String.valueOf(15.0));

        return TemperatureArrange.from(weatherInfoMap);
    }

    private static Stream<Arguments> provideInvalidInfo() {
        return Stream.of(
            Arguments.of("title이 null인 경우",
                new PostSaveRequest(null, "Test Content", VALID_COORDINATE), generateTemperatureArrange(),
                BLANK_POST_TITLE),
            Arguments.of("title이 공백인 경우",
                new PostSaveRequest(" ", "Test Content", VALID_COORDINATE), generateTemperatureArrange(),
                BLANK_POST_TITLE),
            Arguments.of("title이 30자를 넘는 경우",
                new PostSaveRequest("T".repeat(31), "Test Content", VALID_COORDINATE), generateTemperatureArrange(),
                INVALID_POST_TITLE),
            Arguments.of("content가 null인 경우",
                new PostSaveRequest("Test Title", null, VALID_COORDINATE), generateTemperatureArrange(),
                BLANK_POST_CONTENT),
            Arguments.of("content가 공백인 경우",
                new PostSaveRequest("Test Title", " ", VALID_COORDINATE), generateTemperatureArrange(),
                BLANK_POST_CONTENT),
            Arguments.of("content가 500자를 넘는 경우",
                new PostSaveRequest("Test Title", "T".repeat(501), VALID_COORDINATE), generateTemperatureArrange(),
                INVALID_POST_CONTENT),
            Arguments.of("temperatureArrange가 null인 경우",
                new PostSaveRequest("Test Title", "T", VALID_COORDINATE), null, NULL_TEMPERATURE_ARRANGE)
        );
    }

    @Test
    @DisplayName("PostSaveRequest로부터 Post를 생성하는 것에 성공한다.")
    void createPostSuccess() {
        // given
        PostSaveRequest request = new PostSaveRequest("Test Title", "Test Content", VALID_COORDINATE);

        // when
        Post createdPost = Post.from(MOCK_USER, request, IMG_URL, generateTemperatureArrange());

        // then
        assertAll(() -> assertThat(createdPost).hasFieldOrPropertyWithValue("user", MOCK_USER),
            () -> assertThat(createdPost).hasFieldOrPropertyWithValue("title", request.title()),
            () -> assertThat(createdPost).hasFieldOrPropertyWithValue("content", request.content()),
            () -> assertThat(createdPost).hasFieldOrPropertyWithValue("image", IMG_URL),
            () -> assertThat(createdPost).hasFieldOrPropertyWithValue("temperatureArrange",
                generateTemperatureArrange()));
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("provideInvalidInfo")
    @DisplayName("from 메서드로 유효하지 않은 PostSaveRequest로부터 Post를 생성하는 것에 실패한다.")
    void createPostFailWithInvalidInfo(String info, PostSaveRequest postSaveRequest,
                                       TemperatureArrange temperatureArrange,
                                       String message) {
        // given // when
        ThrowingCallable createPost = () -> Post.from(MOCK_USER, postSaveRequest, IMG_URL, temperatureArrange);

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(createPost)
            .withMessage(message);
    }

    @Test
    @DisplayName("from 메서드로 null User로부터 Post를 생성하는 것에 실패한다.")
    void createPostFailWithNullUser() {
        // given
        PostSaveRequest request = new PostSaveRequest("T".repeat(31), "Test Content", VALID_COORDINATE);

        // when
        ThrowingCallable createPost = () -> Post.from(null, request, IMG_URL, generateTemperatureArrange());

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(createPost)
            .withMessage(NULL_WRITER);
    }

}
