package com.backendoori.ootw.like.service;

import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.VALID_COORDINATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import com.backendoori.ootw.exception.UserNotFoundException;
import com.backendoori.ootw.like.domain.Like;
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
import com.backendoori.ootw.weather.domain.TemperatureArrange;
import com.backendoori.ootw.weather.domain.forecast.ForecastCategory;
import net.datafaker.Faker;
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

    static final String POST_NOT_FOUND_MESSAGE = "해당 게시글이 존재하지 않습니다.";
    static final Faker FAKER = new Faker();
    static final int NX = 55;
    static final int NY = 127;

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
            .email(FAKER.internet().emailAddress())
            .password(FAKER.internet().password())
            .nickname(FAKER.internet().username())
            .image(FAKER.internet().url())
            .certified(true)
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
        Like like = Like.builder().user(user).post(post).isLike(true).build();
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
        assertThatThrownBy(() -> likeService.requestLike(userId, postId))
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
        assertThatThrownBy(() -> likeService.requestLike(userId, postId))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessage(UserNotFoundException.DEFAULT_MESSAGE);

    }

    @Test
    @DisplayName("정상적으로 좋아요 개수를 가져오는 경우")
    public void countLikePost() {
        //given
        Long postId = post.getId();
        Long userId = user.getId();
        ;

        //when
        likeService.requestLike(userId, postId);
        // then

        Post findPost = postRepository.findById(postId).get();
        assertThat(findPost.getLikeCnt()).isEqualTo(1);

        likeService.requestLike(userId, postId);

        Post reFindPost = postRepository.findById(postId).get();
        assertThat(reFindPost.getLikeCnt()).isEqualTo(0);
    }

    @Test
    @DisplayName("단일 사용자의 여러 번 좋아요 요청 처리 테스트")
    void testLikeFunctionalityForSingleUserMultipleRequests() throws InterruptedException {
        //when
        int threadCount = 50; // 동시에 실행할 스레드 수
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        // 동일한 사용자로부터 여러 좋아요 요청을 보내는 스레드 실행
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                likeService.requestLike(user.getId(), post.getId());
            });
        }

        executorService.shutdown();
        assertTrue(executorService.awaitTermination(1, TimeUnit.MINUTES));

        // 좋아요 개수 검증
        Post updatedPost = postRepository.findById(post.getId()).get();
        assertThat(updatedPost.getLikeCnt()).isEqualTo(0);
    }

    @Test
    @DisplayName("여러 사용자의 단일 좋아요 요청 처리 테스트")
    void testLikeFunctionalityForMultipleUsersSingleRequest() throws InterruptedException {

        int userCount = 50; // 테스트에 사용할 사용자 수
        ExecutorService executorService = Executors.newFixedThreadPool(userCount);

        // 여러 사용자로부터 각각 한 번씩 좋아요 요청을 보내는 스레드 실행
        for (int i = 0; i < userCount; i++) {
            User multiUser = generateUser();
            userRepository.save(multiUser);

            executorService.submit(() -> {
                likeService.requestLike(multiUser.getId(), post.getId());
            });
        }

        executorService.shutdown();
        assertTrue(executorService.awaitTermination(1, TimeUnit.MINUTES));

        // 좋아요 개수 검증
        Post updatedPost = postRepository.findById(post.getId()).get();
        assertThat(userCount).isEqualTo(updatedPost.getLikeCnt());
    }


}
