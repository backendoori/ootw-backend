package com.backendoori.ootw.like.service;

import static org.assertj.core.api.Assertions.*;

import java.util.NoSuchElementException;
import com.backendoori.ootw.exception.UserNotFoundException;
import com.backendoori.ootw.like.domain.Like;
import com.backendoori.ootw.like.dto.controller.LikeRequest;
import com.backendoori.ootw.like.dto.controller.LikeResponse;
import com.backendoori.ootw.like.repository.LikeRepository;
import com.backendoori.ootw.post.controller.PostController;
import com.backendoori.ootw.post.domain.Post;
import com.backendoori.ootw.post.dto.PostSaveRequest;
import com.backendoori.ootw.post.repository.PostRepository;
import com.backendoori.ootw.post.service.PostService;
import com.backendoori.ootw.security.TokenMockMvcTest;
import com.backendoori.ootw.user.domain.User;
import com.backendoori.ootw.user.repository.UserRepository;
import com.backendoori.ootw.weather.domain.Weather;
import com.backendoori.ootw.weather.dto.WeatherDto;
import net.datafaker.Faker;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LikeServiceTest extends TokenMockMvcTest {

    private static final String POST_NOT_FOUND_MESSAGE = "해당 게시글이 존재하지 않습니다.";
    static Faker faker = new Faker();

    User user;

    User writer;

    Post post;

    @Autowired
    PostController postController;

    @Autowired
    PostService postService;

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    LikeService likeService;


    @BeforeEach
    void setup() {
        likeRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
        user = userRepository.save(generateUser());
        writer = userRepository.save(generateUser());
        post = postRepository.save(generatePost(writer));
        setToken(user.getId());
    }

    @AfterEach
    void cleanup() {
        likeRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    private User generateUser() {
        return User.builder()
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
    @DisplayName("정상적으로 likeDto를 받아 좋아요를 누르면 성공한다.")
    public void likePostSuccess() throws Exception {
        //given
        Long postId = post.getId();
        Long userId = user.getId();

        //when
        LikeResponse response = likeService.requestLike(userId, postId);

        //then
        assertThat(response.postId()).isEqualTo(postId);
        assertThat(response.userId()).isEqualTo(userId);
        assertThat(response.status()).isEqualTo(true);

    }

    @Test
    @DisplayName("이미 좋아요를 누른 게시물의 경우 좋아요가 취소된다.")
    public void likePostCancelSuccess() throws Exception {
        //given
        Like like = Like.builder().user(user).post(post).status(true).build();
        likeRepository.save(like);
        Long postId = post.getId();
        Long userId = user.getId();

        //when
        LikeResponse response = likeService.requestLike(userId, postId);

        //then
        assertThat(response.postId()).isEqualTo(postId);
        assertThat(response.userId()).isEqualTo(userId);
        assertThat(response.status()).isEqualTo(false);

    }

    @ParameterizedTest
    @ValueSource(longs = {-1, 0, Long.MAX_VALUE})
    @DisplayName("존재하지 않는 게시물에 좋아요를 누르는 경우 요청에 실패한다.")
    public void likePostFailWithWrongPostId(Long wrongPostId) {
        //given
        Long postId = wrongPostId;
        Long userId = user.getId();

        //when, then
        assertThatThrownBy(() ->likeService.requestLike(userId, postId))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage(POST_NOT_FOUND_MESSAGE);

    }

    @ParameterizedTest
    @ValueSource(longs = {-1, 0, Long.MAX_VALUE})
    @DisplayName("존재하지 않는 유저에 좋아요를 요청한 경우 요청에 실패한다.")
    public void likePostFailWithWrongUserId(Long wrongUserId) {
        //given
        Long postId = post.getId();
        Long userId = wrongUserId;

        //when, then
        assertThatThrownBy(() ->likeService.requestLike(userId, postId))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessage(UserNotFoundException.DEFAULT_MESSAGE);

    }


}
