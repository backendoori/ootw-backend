package com.backendoori.ootw.like.domain;

import static org.assertj.core.api.Assertions.*;

import com.backendoori.ootw.post.domain.Post;
import com.backendoori.ootw.post.dto.PostSaveRequest;
import com.backendoori.ootw.user.domain.User;
import com.backendoori.ootw.weather.dto.WeatherDto;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

class LikeTest {

    static Faker faker = new Faker();

    private User generateUser() {
        return User.builder()
            .id((long) faker.number().positive())
            .email(faker.internet().emailAddress())
            .password(faker.internet().password())
            .nickname(faker.internet().username())
            .image(faker.internet().url())
            .build();
    }

    private Post generatePost(User user) {
        PostSaveRequest postSaveRequest =
            new PostSaveRequest("title", faker.gameOfThrones().quote(), weatherDtoGenerator());
        return Post.from(user, postSaveRequest, faker.internet().url());
    }

    private WeatherDto weatherDtoGenerator() {
        return new WeatherDto(0.0, -10.0, 10.0, 1, 1);
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
            .status(true)
            .build()).doesNotThrowAnyException();

        assertThatCode(() -> Like.builder()
            .user(user)
            .post(post)
            .status(false)
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
            .status(true)
            .build())
            .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> Like.builder()
            .user(user)
            .post(post)
            .status(false)
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
            .status(true)
            .build())
            .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> Like.builder()
            .user(user)
            .post(post)
            .status(false)
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
            .status(true)
            .build();

        like.updateStatus();
        assertThat(like.getStatus()).isEqualTo(false);

        like.updateStatus();
        assertThat(like.getStatus()).isEqualTo(true);

    }


}
