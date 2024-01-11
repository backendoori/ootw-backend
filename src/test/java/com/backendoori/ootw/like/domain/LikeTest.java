package com.backendoori.ootw.like.domain;

import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.VALID_COORDINATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;
import com.backendoori.ootw.post.domain.Post;
import com.backendoori.ootw.post.dto.request.PostSaveRequest;
import com.backendoori.ootw.user.domain.User;
import com.backendoori.ootw.weather.domain.TemperatureArrange;
import com.backendoori.ootw.weather.domain.forecast.ForecastCategory;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

class LikeTest {

    static final Faker FAKER = new Faker();

    private User generateUser() {
        return User.builder()
            .id((long) FAKER.number().positive())
            .email(FAKER.internet().emailAddress())
            .password(FAKER.internet().password())
            .nickname(FAKER.internet().username())
            .image(FAKER.internet().url())
            .build();
    }

    private static TemperatureArrange generateTemperatureArrange() {
        Map<ForecastCategory, String> weatherInfoMap = new HashMap<>();
        weatherInfoMap.put(ForecastCategory.TMN, String.valueOf(0.0));
        weatherInfoMap.put(ForecastCategory.TMX, String.valueOf(15.0));

        return TemperatureArrange.from(weatherInfoMap);
    }

    private Post generatePost(User user) {
        PostSaveRequest postSaveRequest =
            new PostSaveRequest("title", FAKER.gameOfThrones().quote(), VALID_COORDINATE);
        return Post.from(user, postSaveRequest, FAKER.internet().url(), generateTemperatureArrange());
    }

    @Test
    @DisplayName("정상적으로 Like 객체가 만들어진다.")
    public void makeLikeSuccess() {
        //given
        User user = generateUser();
        User writer = generateUser();
        Post post = generatePost(writer);

        //when, then
        assertThatCode(() -> Like.builder()
            .user(user)
            .post(post)
            .isLike(true)
            .build()).doesNotThrowAnyException();

        assertThatCode(() -> Like.builder()
            .user(user)
            .post(post)
            .isLike(false)
            .build()).doesNotThrowAnyException();

    }

    @ParameterizedTest
    @NullSource
    @DisplayName("post가 null 인 경우 좋아요 객체를 생성 시 예외가 발생한다.")
    public void makeLikeFailByNullPost(Post post) {
        //given
        User user = generateUser();

        //when, then
        assertThatThrownBy(() -> Like.builder()
            .user(user)
            .post(post)
            .isLike(true)
            .build())
            .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> Like.builder()
            .user(user)
            .post(post)
            .isLike(false)
            .build())
            .isInstanceOf(IllegalArgumentException.class);

    }

    @ParameterizedTest
    @NullSource
    @DisplayName("user가 null 인 경우 좋아요 객체를 생성 시 예외가 발생한다.")
    public void makeLikeFailByNullUser(User user) {
        //given
        User writer = generateUser();
        Post post = generatePost(writer);

        //when, then
        assertThatThrownBy(() -> Like.builder()
            .user(user)
            .post(post)
            .isLike(true)
            .build())
            .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> Like.builder()
            .user(user)
            .post(post)
            .isLike(false)
            .build())
            .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    @DisplayName("status 값을 주지 않는 경우 예외가 발생한다.")
    public void makeLikeFailByNullStatus() {
        //given
        User user = generateUser();
        User writer = generateUser();
        Post post = generatePost(writer);

        //when, then
        assertThatThrownBy(() -> Like.builder()
            .user(user)
            .post(post)
            .build())
            .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    @DisplayName("정상적으로 Like status가 업데이트 된다.")
    public void statusUpdateSuccess() {
        //given
        User user = generateUser();
        User writer = generateUser();
        Post post = generatePost(writer);

        //when, then
        Like like = Like.builder()
            .user(user)
            .post(post)
            .isLike(true)
            .build();

        like.updateStatus();
        assertThat(like.getIsLike()).isEqualTo(false);

        like.updateStatus();
        assertThat(like.getIsLike()).isEqualTo(true);

    }


}
