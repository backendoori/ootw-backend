package com.backendoori.ootw.post.service;

import static com.backendoori.ootw.post.validation.Message.BLANK_POST_CONTENT;
import static com.backendoori.ootw.post.validation.Message.BLANK_POST_TITLE;
import static com.backendoori.ootw.post.validation.Message.INVALID_POST_CONTENT;
import static com.backendoori.ootw.post.validation.Message.INVALID_POST_TITLE;
import static com.backendoori.ootw.post.validation.Message.POST_NOT_FOUND;
import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.VALID_COORDINATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import com.backendoori.ootw.common.image.ImageFile;
import com.backendoori.ootw.common.image.ImageService;
import com.backendoori.ootw.common.image.exception.SaveException;
import com.backendoori.ootw.exception.UserNotFoundException;
import com.backendoori.ootw.like.repository.LikeRepository;
import com.backendoori.ootw.like.service.LikeService;
import com.backendoori.ootw.post.domain.Post;
import com.backendoori.ootw.post.dto.request.PostSaveRequest;
import com.backendoori.ootw.post.dto.request.PostUpdateRequest;
import com.backendoori.ootw.post.dto.response.PostReadResponse;
import com.backendoori.ootw.post.dto.response.PostSaveUpdateResponse;
import com.backendoori.ootw.post.dto.response.WriterDto;
import com.backendoori.ootw.post.exception.NoPostPermissionException;
import com.backendoori.ootw.post.exception.ResourceNotExistException;
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
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.TestSecurityContextHolder;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class PostServiceTest {

    static final Faker FAKER = new Faker();
    public static final String IMG_URL = "imageUrl";
    public static final String FILE_NAME = "filename.jpeg";

    User user;

    @Autowired
    private PostService postService;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LikeService likeService;

    @MockBean
    private ImageService imageService;

    @MockBean
    private WeatherService weatherService;

    @BeforeEach
    void setup() {
        likeRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();

        user = userRepository.save(generateUser());
        setAuthentication(user.getId());
    }

    @AfterAll
    void cleanup() {
        likeRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Nested
    @DisplayName("게시글 삭제하기")
    class DeleteTest {

        Post userPost;
        Post otherPost;

        @BeforeEach
        void setup() {
            userPost = postRepository.save(
                Post.from(user, new PostSaveRequest("title", "content", VALID_COORDINATE), null,
                    generateTemperatureArrange()));

            User other = userRepository.save(generateUser());
            otherPost = postRepository.save(
                Post.from(other, new PostSaveRequest("title", "content", VALID_COORDINATE), null,
                    generateTemperatureArrange()));
        }

        @Test
        @DisplayName("게시글 삭제에 성공한다.")
        void deleteSuccess() {
            // given // when // then
            assertDoesNotThrow(() -> postService.delete(userPost.getId()));
        }

        @Test
        @DisplayName("게시글 주인이 아닌 사용자가 게시글 삭제에 실패한다.")
        void deleteFailWithNoPermission() {
            // given // when
            ThrowingCallable deletePost = () -> postService.delete(otherPost.getId());

            // then
            assertThatExceptionOfType(NoPostPermissionException.class)
                .isThrownBy(deletePost)
                .withMessage(NoPostPermissionException.DEFAULT_MESSAGE);
        }

        @Test
        @DisplayName("존재하지 않는 게시글 삭제에 실패한다.")
        void deleteFailWithNonExistPost() {
            // given // when
            ThrowingCallable deletePost = () -> postService.delete(otherPost.getId() + 1);

            // then
            assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(deletePost)
                .withMessage(POST_NOT_FOUND);
        }

    }

    @Nested
    @DisplayName("게시글 수정하기")
    class UpdateTest {

        Post userPost;
        Post otherPost;

        @BeforeEach
        void setup() {
            userPost = postRepository.save(
                Post.from(user, new PostSaveRequest("title", "content", VALID_COORDINATE), null,
                    generateTemperatureArrange()));

            User other = userRepository.save(generateUser());
            otherPost = postRepository.save(
                Post.from(other, new PostSaveRequest("title", "content", VALID_COORDINATE), null,
                    generateTemperatureArrange()));
        }

        @Nested
        @DisplayName("게시글 수정에 성공한다")
        class UpdateSuccess {

            @Test
            @DisplayName(" 게시글 정보와 이미지 수정에 성공한다.")
            void updateAllSuccess() {
                // given
                PostUpdateRequest request = new PostUpdateRequest("Test Title", "Test Content");
                MockMultipartFile postImg = new MockMultipartFile("file", FILE_NAME,
                    "text/plain", "some xml".getBytes());

                given(imageService.upload(postImg)).willReturn(new ImageFile(IMG_URL, FILE_NAME));

                // when
                PostSaveUpdateResponse response = postService.update(userPost.getId(), postImg, request);

                //then
                assertAll(
                    () -> assertThat(response).hasFieldOrPropertyWithValue("title", request.title()),
                    () -> assertThat(response).hasFieldOrPropertyWithValue("content", request.content()),
                    () -> assertThat(response).hasFieldOrPropertyWithValue("image", IMG_URL)
                );
            }

            @Test
            @DisplayName("게시글 정보만 수정에 성공한다.")
            void updatePostUpdateRequestSuccess() {
                // given
                PostUpdateRequest request = new PostUpdateRequest("Test Title", "Test Content");

                // when
                PostSaveUpdateResponse response = postService.update(userPost.getId(), null, request);

                //then
                assertAll(
                    () -> assertThat(response).hasFieldOrPropertyWithValue("title", request.title()),
                    () -> assertThat(response).hasFieldOrPropertyWithValue("content", request.content())
                );
            }

            @Test
            @DisplayName("게시글 이미지만 수정에 성공한다.")
            void updatePostImageSuccess() {
                // given
                MockMultipartFile postImg = new MockMultipartFile("file", FILE_NAME,
                    "text/plain", "some xml".getBytes());
                String IMG_URL = "IMG_URL";

                given(imageService.upload(postImg)).willReturn(new ImageFile(IMG_URL, FILE_NAME));

                // when
                PostSaveUpdateResponse response = postService.update(userPost.getId(), postImg, null);

                //then
                assertThat(response).hasFieldOrPropertyWithValue("image", IMG_URL);
            }

        }

        @Nested
        @DisplayName("게시글 수정에 실패한다")
        class UpdateFail {

            static Stream<Arguments> provideInvalidPostUpdateRequest() {
                return Stream.of(
                    Arguments.of("제목이 null인 경우", new PostUpdateRequest(null, "content"), BLANK_POST_TITLE),
                    Arguments.of("제목이 공백인 경우", new PostUpdateRequest(" ", "content"), BLANK_POST_TITLE),
                    Arguments.of("제목이 30자가 넘는 경우", new PostUpdateRequest("t".repeat(31), "content"),
                        INVALID_POST_TITLE),
                    Arguments.of("내용이 null인 경우", new PostUpdateRequest("title", null), BLANK_POST_CONTENT),
                    Arguments.of("내용이 공백인 경우", new PostUpdateRequest("title", " "), BLANK_POST_CONTENT),
                    Arguments.of("내용이 500자가 넘는 경우", new PostUpdateRequest("title", "t".repeat(501)),
                        INVALID_POST_CONTENT),
                    Arguments.of("제목과 내용이 모두 null인 경우", new PostUpdateRequest(null, null), BLANK_POST_TITLE),
                    Arguments.of("제목과 내용이 모두 공백인 경우", new PostUpdateRequest(" ", " "), BLANK_POST_TITLE)
                );
            }

            @Test
            @DisplayName("게시글 주인이 아닌 사용자가 게시글 수정에 실패한다.")
            void updateFailWithPermission() {
                // given
                PostUpdateRequest request = new PostUpdateRequest("Test Title", "Test Content");
                MockMultipartFile postImg = new MockMultipartFile("file", FILE_NAME,
                    "text/plain", "some xml".getBytes());

                // when
                ThrowingCallable updatePost = () -> postService.update(otherPost.getId(), postImg, request);

                //then
                assertThatExceptionOfType(NoPostPermissionException.class)
                    .isThrownBy(updatePost)
                    .withMessage(NoPostPermissionException.DEFAULT_MESSAGE);
            }

            @Test
            @DisplayName("존재하지 않는 게시글 수정에 실패한다.")
            void updateFailWithNonExistPost() {
                // given
                PostUpdateRequest request = new PostUpdateRequest("Test Title", "Test Content");
                MockMultipartFile postImg = new MockMultipartFile("file", FILE_NAME,
                    "text/plain", "some xml".getBytes());

                // when
                ThrowingCallable updatePost = () -> postService.update(otherPost.getId() + 1, postImg, request);

                //then
                assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(updatePost)
                    .withMessage(POST_NOT_FOUND);
            }

            @Test
            @DisplayName("수정할 리소스를 전혀 보내지 않으면 실패한다.")
            void updateFailWithNoResource() {
                // given // when
                ThrowingCallable updatePost = () -> postService.update(userPost.getId(), null, null);

                //then
                assertThatExceptionOfType(ResourceNotExistException.class)
                    .isThrownBy(updatePost)
                    .withMessage(ResourceNotExistException.DEFAULT_MESSAGE);
            }

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("provideInvalidPostUpdateRequest")
            @DisplayName("수정할 게시글 정보를 보냈는데 제목이나 내용이 null이거나 공백이면 수정에 실패한다.")
            void updateFailWithNullPostUpdateRequest(String testCase, PostUpdateRequest request, String message) {
                // given // when
                ThrowingCallable updatePost = () -> postService.update(userPost.getId(), null, request);

                //then
                assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(updatePost)
                    .withMessage(message);
            }

        }

    }

    @Nested
    @DisplayName("게시글 저장하기")
    class SaveTest {

        @Test
        @DisplayName("게시글 저장에 성공한다.")
        void saveSuccess() throws IOException {
            // given
            PostSaveRequest request = new PostSaveRequest("Test Title", "Test Content", VALID_COORDINATE);
            MockMultipartFile postImg = new MockMultipartFile("file", FILE_NAME,
                "image/jpeg", "some xml".getBytes());

            given(imageService.upload(postImg)).willReturn(
                new ImageFile("http://mock.server.com/filename.jpeg", FILE_NAME));
            given(weatherService.getCurrentTemperatureArrange(VALID_COORDINATE)).willReturn(
                generateTemperatureArrange());

            // when
            PostSaveUpdateResponse postSaveResponse = postService.save(request, postImg);

            //then
            assertThat(postSaveResponse).hasFieldOrPropertyWithValue("title", request.title());
            assertThat(postSaveResponse).hasFieldOrPropertyWithValue("content", request.content());
            assertThat(postSaveResponse).hasFieldOrPropertyWithValue("image",
                imageService.upload(postImg).url());
            assertThat(postSaveResponse).hasFieldOrPropertyWithValue("temperatureArrange",
                TemperatureArrangeDto.from(generateTemperatureArrange()));
        }

        @Test
        @DisplayName("저장된 유저가 아닌 경우 게시글 저장에 실패한다.")
        void saveFailUserNotFound() {
            // given
            setAuthentication(user.getId() + 1);

            PostSaveRequest postSaveRequest = new PostSaveRequest("Test Title", "Test Content", VALID_COORDINATE);
            MockMultipartFile postImg = new MockMultipartFile("file", FILE_NAME,
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
            MockMultipartFile postImg = new MockMultipartFile("file", FILE_NAME,
                "image/jpeg", "some xml".getBytes());
            given(imageService.upload(postImg)).willReturn(
                new ImageFile("http://mock.server.com/filename.jpeg", FILE_NAME));

            // when, then
            assertThrows(SaveException.class,
                () -> postService.save(postSaveRequest, postImg));
        }

    }

    @Nested
    @DisplayName("게시글 단건 조회하기")
    class GetDetailByPostIdTest {

        PostSaveUpdateResponse postSaveResponse;

        @BeforeEach
        void setUp() {
            Post savedPost = postRepository.save(
                Post.from(user, new PostSaveRequest("Test Title", "Test Content", VALID_COORDINATE), "IMG_URL",
                    generateTemperatureArrange()));
            postSaveResponse = PostSaveUpdateResponse.from(savedPost);
        }

        @Test
        @DisplayName("게시글 단건 조회에 성공한다.")
        void getDetailByPostIdSuccess() {
            // given
            setAuthentication(user.getId());
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

        @Test
        @DisplayName("로그인 후 좋아요 여부가 포함된 게시글 단건 조회에 성공한다.")
        void getAllSuccessWithLogin() {
            // given
            List<Post> postList = postRepository.findAll();
            setAuthentication(user.getId());

            for (Post likePost : postList) {
                likeService.requestLike(user.getId(), likePost.getId());
            }

            // when
            PostReadResponse findPost = postService.getDetailByPostId(postSaveResponse.postId());

            // then
            assertThat(findPost.getIsLike()).isEqualTo(1);
            assertThat(findPost.getLikeCnt()).isEqualTo(1);

        }

        @Test
        @DisplayName("로그인은 했지만 좋아요를 누르지 않은 경우에도 게시글 단건 조회에 성공한다.")
        void getAllSuccessWithLoginNoLike() {
            // given
            setAuthentication(user.getId());

            // when
            PostReadResponse findPost = postService.getDetailByPostId(postSaveResponse.postId());

            // then
            assertThat(findPost.getIsLike()).isEqualTo(0);
            assertThat(findPost.getLikeCnt()).isEqualTo(0);
        }

        @Test
        @DisplayName("로그인은 안했을 때 좋아요를 누르지 않은 경우에도 게시글 단건 조회에 성공한다.")
        void getAllSuccessWithoutLogin() {
            // given, when
            setAnonymousAuthentication();
            PostReadResponse findPost = postService.getDetailByPostId(postSaveResponse.postId());

            // then
            assertThat(findPost.getIsLike()).isEqualTo(0);
            assertThat(findPost.getLikeCnt()).isEqualTo(0);

        }

    }

    @Nested
    @DisplayName("게시글 목록 조회하기")
    class GetAllTest {

        static final Integer SAVE_COUNT = 10;

        @BeforeEach
        void setUp() {
            for (int i = 0; i < SAVE_COUNT; i++) {
                postRepository.save(
                    Post.from(user, new PostSaveRequest("Test Title", "Test Content", VALID_COORDINATE), "IMG_URL",
                        generateTemperatureArrange()));
            }
        }

        @AfterEach
        void clearUp() {
            for (int i = 0; i < SAVE_COUNT; i++) {
                likeRepository.deleteAll();
                postRepository.deleteAll();
                userRepository.deleteAll();
            }
        }

        @Test
        @DisplayName("게시글 목록 최신순(default) 조회에 성공한다.")
        void getAllSuccess() {
            // given, when
            setAnonymousAuthentication();
            List<PostReadResponse> posts = postService.getAll();
            List<PostReadResponse> expectedSortedPosts = posts.stream().sorted((post1, post2) -> {
                if (post1.getCreatedAt().isAfter(post2.getCreatedAt())) {
                    return -1;
                }
                if (post1.getCreatedAt().isBefore(post2.getCreatedAt())) {
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

        @Test
        @DisplayName("로그인 후 좋아요 여부가 포함된 게시글 목록 최신순 조회에 성공한다.")
        void getAllSuccessWithLogin() {
            // given
            List<Post> postList = postRepository.findAll();

            for (Post likePost : postList) {
                likeService.requestLike(user.getId(), likePost.getId());
            }

            // when
            List<PostReadResponse> posts = postService.getAll();

            // then
            for (PostReadResponse response : posts) {
                assertThat(response.getIsLike()).isEqualTo(1);
                assertThat(response.getLikeCnt()).isEqualTo(1);
            }

        }

        @Test
        @DisplayName("로그인은 했지만 좋아요를 누르지 않은 경우에도 게시글 목록 최신순 조회에 성공한다.")
        void getAllSuccessWithLoginNoLike() {
            // given, when
            List<PostReadResponse> posts = postService.getAll();

            // then
            for (PostReadResponse response : posts) {
                assertThat(response.getIsLike()).isEqualTo(0);
            }

        }

        @Test
        @DisplayName("다른 사람이 좋아요를 눌렀어도 로그인을 안한 경우에도 게시글 목록 최신순 조회에 성공한다.")
        void getAllSuccessWithLikedPost() {
            // given
            List<Post> postList = postRepository.findAll();

            for (Post likePost : postList) {
                likeService.requestLike(user.getId(), likePost.getId());
            }
            setAnonymousAuthentication();

            // when
            List<PostReadResponse> posts = postService.getAll();

            // then
            for (PostReadResponse response : posts) {
                assertThat(response.getIsLike()).isEqualTo(0);
                assertThat(response.getLikeCnt()).isEqualTo(1);
            }

        }

    }

    private static void setAnonymousAuthentication() {
        SecurityContextHolder.getContext()
            .setAuthentication(new AnonymousAuthenticationToken("key", "anonymousUser",
                Collections.singleton(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));
    }

    private User generateUser() {
        return User.builder()
            .id((long) FAKER.number().positive())
            .email(FAKER.internet().emailAddress())
            .password(FAKER.internet().password())
            .nickname(FAKER.internet().username())
            .profileImageUrl(FAKER.internet().url())
            .certified(true)
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
