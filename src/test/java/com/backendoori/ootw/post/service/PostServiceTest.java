package com.backendoori.ootw.post.service;

import static com.backendoori.ootw.post.validation.Message.POST_NOT_FOUND;
import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.VALID_COORDINATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import com.backendoori.ootw.common.image.ImageService;
import com.backendoori.ootw.exception.UserNotFoundException;
import com.backendoori.ootw.post.domain.Post;
import com.backendoori.ootw.post.dto.PostReadResponse;
import com.backendoori.ootw.post.dto.PostSaveRequest;
import com.backendoori.ootw.post.dto.PostSaveResponse;
import com.backendoori.ootw.post.dto.WriterDto;
import com.backendoori.ootw.post.repository.PostRepository;
import com.backendoori.ootw.user.domain.User;
import com.backendoori.ootw.user.repository.UserRepository;
import com.backendoori.ootw.weather.domain.TemperatureArrange;
import com.backendoori.ootw.weather.domain.forecast.ForecastCategory;
import com.backendoori.ootw.weather.dto.TemperatureArrangeDto;
import com.backendoori.ootw.weather.service.WeatherService;
import net.datafaker.Faker;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.test.context.TestSecurityContextHolder;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class PostServiceTest {

    static final Faker FAKER = new Faker();

    User user;

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private ImageService imageService;

    @MockBean
    private WeatherService weatherService;

    @BeforeEach
    void setup() {
        postRepository.deleteAll();
        userRepository.deleteAll();

        user = userRepository.save(generateUser());

        setAuthentication(user.getId());
    }

    @AfterAll
    void cleanup() {
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("게시글 저장 테스트")
    class SaveTest {

        @Test
        @DisplayName("게시글 저장에 성공한다.")
        void saveSuccess() {
            // given
            PostSaveRequest request = new PostSaveRequest("Test Title", "Test Content", VALID_COORDINATE);
            MockMultipartFile postImg = new MockMultipartFile("file", "filename.txt",
                "text/plain", "some xml".getBytes());

            given(imageService.uploadImage(postImg)).willReturn("imgUrl");
            given(weatherService.getCurrentTemperatureArrange(VALID_COORDINATE)).willReturn(
                generateTemperatureArrange());

            // when
            PostSaveResponse postSaveResponse = postService.save(request, postImg);

            //then
            assertAll(
                () -> assertThat(postSaveResponse).hasFieldOrPropertyWithValue("title", request.title()),
                () -> assertThat(postSaveResponse).hasFieldOrPropertyWithValue("content", request.content()),
                () -> assertThat(postSaveResponse).hasFieldOrPropertyWithValue("image",
                    imageService.uploadImage(postImg)),
                () -> assertThat(postSaveResponse).hasFieldOrPropertyWithValue("temperatureArrange",
                    TemperatureArrangeDto.from(generateTemperatureArrange()))
            );
        }

        @Test
        @DisplayName("저장된 유저가 아닌 경우 게시글 저장에 실패한다.")
        void saveFailUserNotFound() {
            // given
            setAuthentication(user.getId() + 1);

            PostSaveRequest postSaveRequest = new PostSaveRequest("Test Title", "Test Content", VALID_COORDINATE);
            MockMultipartFile postImg = new MockMultipartFile("file", "filename.txt",
                "text/plain", "some xml".getBytes());

            // when
            ThrowingCallable savePost = () -> postService.save(postSaveRequest, postImg);

            // then
            assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(savePost)
                .withMessage(UserNotFoundException.DEFAULT_MESSAGE);
        }

        static Stream<Arguments> provideInvalidPostInfo() {
            String validTitle = "title";
            String validContent = "content";
            return Stream.of(
                Arguments.of(null, validContent),
                Arguments.of(validTitle, null),
                Arguments.of("", validContent),
                Arguments.of(validTitle, ""),
                Arguments.of(" ", validContent),
                Arguments.of(validTitle, " "),
                Arguments.of("a".repeat(40), validContent),
                Arguments.of(validTitle, "a".repeat(600))
            );
        }

        @ParameterizedTest(name = "[{index}] 제목이 {0}이고 내용이 {1}인 경우")
        @MethodSource("provideInvalidPostInfo")
        @DisplayName("유효하지 않은 값(게시글 정보)가 들어갈 경우 게시글 저장에 실패한다.")
        void saveFailWithInvalidValue(String title, String content) {
            // given
            given(weatherService.getCurrentTemperatureArrange(VALID_COORDINATE)).willReturn(
                generateTemperatureArrange());

            PostSaveRequest postSaveRequest = new PostSaveRequest(title, content, VALID_COORDINATE);
            MockMultipartFile postImg = new MockMultipartFile("file", "filename.txt",
                "text/plain", "some xml".getBytes());

            // when, then
            assertThrows(IllegalArgumentException.class,
                () -> postService.save(postSaveRequest, postImg));
        }

    }

    @Nested
    @DisplayName("게시글 단건 조회하기")
    class GetDetailByPostId {

        PostSaveResponse postSaveResponse;

        @BeforeEach
        void setUp() {
            Post savedPost = postRepository.save(
                Post.from(user, new PostSaveRequest("Test Title", "Test Content", VALID_COORDINATE), "imgUrl",
                    generateTemperatureArrange()));
            postSaveResponse = PostSaveResponse.from(savedPost);
        }

        @Test
        @DisplayName("게시글 단건 조회에 성공한다.")
        void getDetailByPostIdSuccess() {
            // given
            WriterDto savedPostWriter = WriterDto.from(user);

            // when
            PostReadResponse postDetailInfo = postService.getDetailByPostId(postSaveResponse.postId());

            // then
            assertAll(
                () -> assertThat(postDetailInfo).hasFieldOrPropertyWithValue("postId", postSaveResponse.postId()),
                () -> assertThat(postDetailInfo).hasFieldOrPropertyWithValue("writer", savedPostWriter),
                () -> assertThat(postDetailInfo).hasFieldOrPropertyWithValue("title", postSaveResponse.title()),
                () -> assertThat(postDetailInfo).hasFieldOrPropertyWithValue("content", postSaveResponse.content()),
                () -> assertThat(postDetailInfo).hasFieldOrPropertyWithValue("image", postSaveResponse.image()),
                () -> assertThat(postDetailInfo)
                    .hasFieldOrPropertyWithValue("createdAt", postSaveResponse.createdAt()),
                () -> assertThat(postDetailInfo)
                    .hasFieldOrPropertyWithValue("updatedAt", postSaveResponse.updatedAt()),
                () -> assertThat(postDetailInfo).hasFieldOrPropertyWithValue("temperatureArrange",
                    postSaveResponse.temperatureArrange())
            );
        }

        @Test
        @DisplayName("저장되지 않은 게시글 Id로 요청할 경우 게시글 단건 조회에 실패한다.")
        void getDetailByPostIdFailNotSavedPost() {
            // given, when.
            ThrowingCallable getDetailByPostId = () -> postService.getDetailByPostId(postSaveResponse.postId() + 1);

            // then
            assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(getDetailByPostId)
                .withMessage(POST_NOT_FOUND);
        }

    }

    @Nested
    @DisplayName("게시글 목록 조회하기")
    class GetAll {

        static final Integer SAVE_COUNT = 10;

        @BeforeEach
        void setUp() {
            for (int i = 0; i < SAVE_COUNT; i++) {
                Post savedPost = postRepository.save(
                    Post.from(user, new PostSaveRequest("Test Title", "Test Content", VALID_COORDINATE), "imgUrl",
                        generateTemperatureArrange()));
            }
        }

        @Test
        @DisplayName("게시글 목록 최신순(default) 조회에 성공한다.")
        void getAllSuccess() {
            // given, when
            List<PostReadResponse> posts = postService.getAll();
            List<PostReadResponse> expectedSortedPosts = posts.stream().sorted((post1, post2) -> {
                if (post1.createdAt().isAfter(post2.createdAt())) {
                    return -1;
                }
                if (post1.createdAt().isBefore(post2.createdAt())) {
                    return 1;
                }
                return 0;
            }).toList();

            // then
            assertAll(
                () -> assertThat(posts.size()).isEqualTo(SAVE_COUNT),
                () -> assertThat(posts).isEqualTo(expectedSortedPosts)
            );
        }

    }

    private User generateUser() {
        return User.builder()
            .id((long) FAKER.number().positive())
            .email(FAKER.internet().emailAddress())
            .password(FAKER.internet().password())
            .nickname(FAKER.internet().username())
            .image(FAKER.internet().url())
            .build();
    }

    private TemperatureArrange generateTemperatureArrange() {
        Map<ForecastCategory, String> weatherInfoMap = new HashMap<>();
        weatherInfoMap.put(ForecastCategory.TMN, String.valueOf(0.0));
        weatherInfoMap.put(ForecastCategory.TMX, String.valueOf(15.0));

        return TemperatureArrange.from(weatherInfoMap);
    }

    private void setAuthentication(long userId) {
        TestSecurityContextHolder
            .getContext()
            .setAuthentication(new TestingAuthenticationToken(userId, null));
    }

}
