package com.backendoori.ootw.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.NoSuchElementException;
import com.backendoori.ootw.common.image.ImageService;
import com.backendoori.ootw.exception.UserNotFoundException;
import com.backendoori.ootw.post.dto.PostReadResponse;
import com.backendoori.ootw.post.dto.PostSaveRequest;
import com.backendoori.ootw.post.dto.PostSaveResponse;
import com.backendoori.ootw.post.dto.WeatherDto;
import com.backendoori.ootw.post.dto.WriterDto;
import com.backendoori.ootw.post.repository.PostRepository;
import com.backendoori.ootw.user.domain.User;
import com.backendoori.ootw.user.repository.UserRepository;
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
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.test.context.TestSecurityContextHolder;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class PostServiceTest {

    static Faker faker = new Faker();

    User user;

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private ImageService imageService;

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
            WeatherDto weatherDto =
                new WeatherDto(0.0, -10.0, 10.0, 1, 1);
            PostSaveRequest request =
                new PostSaveRequest("Test Title", "Test Content", weatherDto);
            MockMultipartFile postImg = new MockMultipartFile("file", "filename.txt",
                "text/plain", "some xml".getBytes());

            given(imageService.uploadImage(postImg)).willReturn("imgUrl");

            // when
            PostSaveResponse postSaveResponse = postService.save(request, postImg);

            //then
            assertAll(
                () -> assertThat(postSaveResponse).hasFieldOrPropertyWithValue("title", request.title()),
                () -> assertThat(postSaveResponse).hasFieldOrPropertyWithValue("content", request.content()),
                () -> assertThat(postSaveResponse).hasFieldOrPropertyWithValue("image",
                    imageService.uploadImage(postImg)),
                () -> assertThat(postSaveResponse).hasFieldOrPropertyWithValue("weather", request.weather())
            );
        }

        @Test
        @DisplayName("저장된 유저가 아닌 경우 게시글 저장에 실패한다.")
        void saveFailUserNotFound() {
            // given
            setAuthentication(user.getId() + 1);

            WeatherDto weatherDto =
                new WeatherDto(0.0, -10.0, 10.0, 1, 1);
            PostSaveRequest postSaveRequest =
                new PostSaveRequest("Test Title", "Test Content", weatherDto);
            MockMultipartFile postImg = new MockMultipartFile("file", "filename.txt",
                "text/plain", "some xml".getBytes());

            // when
            ThrowingCallable savePost = () -> postService.save(postSaveRequest, postImg);

            // then
            assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(savePost)
                .withMessage(UserNotFoundException.DEFAULT_MESSAGE);
        }

        // TODO: 그 외 파라미터도 일일이 테스트 할까 고민!(일단 보류)
        @ParameterizedTest(name = "[{index}] 현재 기온이 {0}인 경우")
        @ValueSource(doubles = {-900.0, 900.0})
        @NullSource
        @DisplayName("유효하지 않은 값(현재 기온)이 들어갈 경우 게시글 저장에 실패한다.")
        void saveFailInvalidValue(Double currentTemperature) {
            // given
            WeatherDto weatherDto =
                new WeatherDto(currentTemperature, -10.0, 10.0, 1, 1);
            PostSaveRequest postSaveRequest =
                new PostSaveRequest("Test Title", "Test Content", weatherDto);
            MockMultipartFile postImg = new MockMultipartFile("file", "filename.txt",
                "text/plain", "some xml".getBytes());

            // when, then
            assertThrows(IllegalArgumentException.class,
                () -> postService.save(postSaveRequest, postImg));
        }

    }

    // TODO: 조회용 setUp()을 미리 만들어 놓아도 좋을 것 같다.
    @Nested
    @DisplayName("게시글 단건 조회하기")
    class GetDetailByPostId {

        PostSaveResponse savedPost;

        @BeforeEach
        void setUp() {
            WeatherDto weatherDto =
                new WeatherDto(0.0, -10.0, 10.0, 1, 1);
            MockMultipartFile postImg = new MockMultipartFile("file", "filename.txt",
                "text/plain", "some xml".getBytes());
            savedPost = postService.save(
                new PostSaveRequest("Test Title", "Test Content", weatherDto), postImg);
        }

        @Test
        @DisplayName("게시글 단건 조회에 성공한다.")
        void getDetailByPostIdSuccess() {
            // given
            WriterDto savedPostWriter = WriterDto.from(user);

            // when
            PostReadResponse postDetailInfo = postService.getDetailByPostId(savedPost.postId());

            // then
            assertAll(
                () -> assertThat(postDetailInfo).hasFieldOrPropertyWithValue("postId", savedPost.postId()),
                () -> assertThat(postDetailInfo).hasFieldOrPropertyWithValue("writer", savedPostWriter),
                () -> assertThat(postDetailInfo).hasFieldOrPropertyWithValue("title", savedPost.title()),
                () -> assertThat(postDetailInfo).hasFieldOrPropertyWithValue("content", savedPost.content()),
                () -> assertThat(postDetailInfo).hasFieldOrPropertyWithValue("image", savedPost.image()),
                () -> assertThat(postDetailInfo)
                    .hasFieldOrPropertyWithValue("createdAt", savedPost.createdAt()),
                () -> assertThat(postDetailInfo)
                    .hasFieldOrPropertyWithValue("updatedAt", savedPost.updatedAt()),
                () -> assertThat(postDetailInfo).hasFieldOrPropertyWithValue("weather", savedPost.weather())
            );
        }

        @Test
        @DisplayName("저장되지 않은 게시글 Id로 요청할 경우 게시글 단건 조회에 실패한다.")
        void getDatailByPostIdFailNotSavedPost() {
            // given, when. then
            assertThrows(NoSuchElementException.class,
                () -> postService.getDetailByPostId(savedPost.postId() + 1));
        }

    }

    @Nested
    @DisplayName("게시글 목록 조회하기")
    class GetAll {

        static final Integer SAVE_COUNT = 10;

        @BeforeEach
        void setUp() {

            WeatherDto weatherDto =
                new WeatherDto(0.0, -10.0, 10.0, 1, 1);
            MockMultipartFile postImg = new MockMultipartFile("file", "filename.txt",
                "text/plain", "some xml".getBytes());
            PostSaveRequest request =
                new PostSaveRequest("Test Title", "Test Content", weatherDto);

            for (int i = 0; i < SAVE_COUNT; i++) {
                postService.save(request, postImg);
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
            .id((long) faker.number().positive())
            .email(faker.internet().emailAddress())
            .password(faker.internet().password())
            .nickname(faker.internet().username())
            .image(faker.internet().url())
            .build();
    }

    private void setAuthentication(long userId) {
        TestSecurityContextHolder
            .getContext()
            .setAuthentication(new TestingAuthenticationToken(userId, null));
    }

}
