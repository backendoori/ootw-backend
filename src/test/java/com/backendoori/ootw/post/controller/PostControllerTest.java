package com.backendoori.ootw.post.controller;

import static com.backendoori.ootw.post.validation.Message.POST_NOT_FOUND;
import static com.backendoori.ootw.security.jwt.JwtAuthenticationFilter.TOKEN_HEADER;
import static com.backendoori.ootw.security.jwt.JwtAuthenticationFilter.TOKEN_PREFIX;
import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.VALID_COORDINATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import com.backendoori.ootw.exception.PermissionException;
import com.backendoori.ootw.post.domain.Post;
import com.backendoori.ootw.post.dto.request.PostSaveRequest;
import com.backendoori.ootw.post.dto.response.PostReadResponse;
import com.backendoori.ootw.post.dto.response.PostSaveUpdateResponse;
import com.backendoori.ootw.post.repository.PostRepository;
import com.backendoori.ootw.post.service.PostService;
import com.backendoori.ootw.security.TokenMockMvcTest;
import com.backendoori.ootw.user.domain.User;
import com.backendoori.ootw.user.repository.UserRepository;
import com.backendoori.ootw.weather.domain.TemperatureArrange;
import com.backendoori.ootw.weather.domain.forecast.ForecastCategory;
import com.backendoori.ootw.weather.service.WeatherService;
import com.fasterxml.jackson.core.JsonProcessingException;
import net.datafaker.Faker;
import org.jetbrains.annotations.NotNull;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.test.context.TestSecurityContextHolder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@TestInstance(Lifecycle.PER_CLASS)
class PostControllerTest extends TokenMockMvcTest {

    static final Faker FAKER = new Faker();
    public static final String IMG_URL = "imageUrl";
    public static final String BASE_URL = "http://localhost:8080/api/v1/posts";
    public static final String ORIGINAL_FILE_NAME = "filename.jpeg";
    public static final String FILE_NAME = "postImg";
    public static final String CONTENT = "CONTENT";
    public static final String TITLE = "TITLE";

    @NotNull
    private static MockMultipartFile getPostImg(String originalFileName, String mediaType) {
        return new MockMultipartFile(FILE_NAME, originalFileName, mediaType, "some xml".getBytes());
    }

    User user;

    @Autowired
    PostController postController;

    @Autowired
    PostService postService;

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @MockBean
    WeatherService weatherService;

    @BeforeEach
    void setup() {
        postRepository.deleteAll();
        userRepository.deleteAll();

        user = userRepository.save(generateUser());
        setToken(user.getId());
    }

    @AfterAll
    void cleanup() {
        postRepository.deleteAll();
        userRepository.deleteAll();
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

    @NotNull
    private MockMultipartFile getRequestJson(String title, String content) throws JsonProcessingException {
        PostSaveRequest postSaveRequest =
            new PostSaveRequest(title, content, VALID_COORDINATE);

        return new MockMultipartFile("request", "request.json", MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(postSaveRequest));
    }

    @NotNull
    private static RequestPostProcessor makeRequestMethodToPut() {
        return req -> {
            req.setMethod("PUT");
            return req;
        };
    }

    @Nested
    @DisplayName("게시글 삭제하기")
    class DeleteTest {

        Post userPost;
        Post otherPost;

        @BeforeEach
        void setup() {
            userPost = postRepository.save(
                Post.from(user, new PostSaveRequest(TITLE, CONTENT, VALID_COORDINATE), null,
                    generateTemperatureArrange()));

            User other = userRepository.save(generateUser());
            otherPost = postRepository.save(
                Post.from(other, new PostSaveRequest(TITLE, CONTENT, VALID_COORDINATE), null,
                    generateTemperatureArrange()));
        }

        @Test
        @DisplayName("게시글 삭제에 성공한다.")
        void deleteSuccess() throws Exception {
            // given // when
            MockHttpServletRequestBuilder requestBuilder =
                delete(BASE_URL + "/" + userPost.getId())
                    .header(TOKEN_HEADER, TOKEN_PREFIX + token);

            // then
            mockMvc.perform(requestBuilder)
                .andExpect(status().isNoContent())
                .andReturn();
        }

        @Test
        @DisplayName("로그인을 안한 사용자는 게시글 삭제에 접근이 불가하다.")
        void deleteFaildeleteFailWithUnauthorizedUser() throws Exception {
            // given // when
            MockHttpServletRequestBuilder requestBuilder =
                delete(BASE_URL + "/" + userPost.getId());

            // then
            mockMvc.perform(requestBuilder)
                .andExpect(status().isUnauthorized())
                .andReturn();
        }

        @Test
        @DisplayName("게시글 주인이 아닌 사용자가 게시글 삭제에 실패한다.")
        void deleteFailWithNoPermission() throws Exception {
            // given // when
            MockHttpServletRequestBuilder requestBuilder =
                delete(BASE_URL + "/" + otherPost.getId())
                    .header(TOKEN_HEADER, TOKEN_PREFIX + token);

            // then
            mockMvc.perform(requestBuilder)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is(PermissionException.DEFAULT_MESSAGE)))
                .andReturn();
        }

        @Test
        @DisplayName("존재하지 않는 게시글 삭제에 실패한다.")
        void deleteFailWithNonExistPost() throws Exception {
            // given // when
            MockHttpServletRequestBuilder requestBuilder =
                delete(BASE_URL + "/" + otherPost.getId() + 1)
                    .header(TOKEN_HEADER, TOKEN_PREFIX + token);

            // then
            mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(POST_NOT_FOUND)))
                .andReturn();
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
                Post.from(user, new PostSaveRequest(TITLE, CONTENT, VALID_COORDINATE), null,
                    generateTemperatureArrange()));

            User other = userRepository.save(generateUser());
            otherPost = postRepository.save(
                Post.from(other, new PostSaveRequest(TITLE, CONTENT, VALID_COORDINATE), null,
                    generateTemperatureArrange()));
        }

        @Nested
        @DisplayName("게시글 수정에 성공한다")
        class UpdateSuccess {

            static Stream<Arguments> provideImageTypes() {
                return Stream.of(
                    Arguments.of("image.jpeg", MediaType.IMAGE_JPEG_VALUE),
                    Arguments.of("image.gif", MediaType.IMAGE_GIF_VALUE),
                    Arguments.of("image.png", MediaType.IMAGE_PNG_VALUE)
                );
            }

            @ParameterizedTest(name = "[{index}]: 아이템 타입이 {0}인 경우에 저장에 성공한다.")
            @MethodSource("provideImageTypes")
            @DisplayName(" 게시글 정보와 이미지 수정에 성공한다.")
            void updateAllSuccess(String originalFileName, String mediaType) throws Exception {
                // given
                MockMultipartFile request = getRequestJson(TITLE, CONTENT);
                MockMultipartFile postImg = getPostImg(originalFileName, mediaType);

                // when
                MockHttpServletRequestBuilder requestBuilder = multipart(BASE_URL + "/" + userPost.getId())
                    .file(request)
                    .file(postImg)
                    .header(TOKEN_HEADER, TOKEN_PREFIX + token)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .with(makeRequestMethodToPut());

                // then
                MockHttpServletResponse response = mockMvc.perform(requestBuilder)
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn()
                    .getResponse();

                assertThat(response.getHeader("location")).contains("/api/v1/posts/");
            }

            @Test
            @DisplayName("게시글 정보 수정에 성공한다.")
            void updatePostUpdateRequestSuccess() throws Exception {
                // given
                MockMultipartFile request = getRequestJson(TITLE, CONTENT);

                // when
                MockHttpServletRequestBuilder requestBuilder = multipart(BASE_URL + "/" + userPost.getId())
                    .file(request)
                    .header(TOKEN_HEADER, TOKEN_PREFIX + token)
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .with(makeRequestMethodToPut());

                // then
                MockHttpServletResponse response = mockMvc.perform(requestBuilder)
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn()
                    .getResponse();

                assertThat(response.getHeader("location")).contains("/api/v1/posts/");
            }

        }

        @Nested
        @DisplayName("게시글 수정에 실패한다")
        class UpdateFail {

            static Stream<Arguments> provideInvalidPostInfo() {
                return Stream.of(
                    Arguments.of(null, CONTENT),
                    Arguments.of(TITLE, null),
                    Arguments.of("", CONTENT),
                    Arguments.of(TITLE, ""),
                    Arguments.of(" ", CONTENT),
                    Arguments.of(TITLE, " "),
                    Arguments.of("a".repeat(40), CONTENT),
                    Arguments.of(TITLE, "a".repeat(600))
                );
            }

            static Stream<Arguments> provideInvalidFile() {
                return Stream.of(
                    Arguments.of("file.md", MediaType.TEXT_MARKDOWN_VALUE),
                    Arguments.of("file.html", MediaType.TEXT_HTML_VALUE),
                    Arguments.of("file.pdf", MediaType.APPLICATION_PDF_VALUE),
                    Arguments.of("file.txt", MediaType.TEXT_PLAIN_VALUE)
                );
            }

            @Test
            @DisplayName("로그인을 안한 사용자는 게시글 수정에 접근이 불가하다.")
            void updateFailWithUnauthorizedUser() throws Exception {
                // given
                MockMultipartFile request = getRequestJson(TITLE, CONTENT);
                MockMultipartFile postImg = getPostImg(ORIGINAL_FILE_NAME, MediaType.IMAGE_JPEG_VALUE);

                // when
                MockHttpServletRequestBuilder requestBuilder = multipart(BASE_URL + "/" + userPost.getId())
                    .file(request)
                    .file(postImg)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .with(makeRequestMethodToPut());

                // then
                mockMvc.perform(requestBuilder)
                    .andExpect(status().isUnauthorized())
                    .andReturn();
            }

            @Test
            @DisplayName("게시글 주인이 아닌 사용자가 게시글 수정에 실패한다.")
            void updateFailWithPermission() throws Exception {
                // given
                MockMultipartFile request = getRequestJson(TITLE, CONTENT);
                MockMultipartFile postImg = getPostImg(ORIGINAL_FILE_NAME, MediaType.IMAGE_JPEG_VALUE);

                // when
                MockHttpServletRequestBuilder requestBuilder = multipart(BASE_URL + "/" + otherPost.getId())
                    .file(request)
                    .file(postImg)
                    .header(TOKEN_HEADER, TOKEN_PREFIX + token)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .with(makeRequestMethodToPut());

                // then
                mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message", is(PermissionException.DEFAULT_MESSAGE)))
                    .andReturn();
            }

            @Test
            @DisplayName("존재하지 않는 게시글 수정에 실패한다.")
            void updateFailWithNonExistPost() throws Exception {
                // given
                MockMultipartFile request = getRequestJson(TITLE, CONTENT);
                MockMultipartFile postImg = getPostImg(ORIGINAL_FILE_NAME, MediaType.IMAGE_JPEG_VALUE);

                // when
                MockHttpServletRequestBuilder requestBuilder = multipart(BASE_URL + "/" + otherPost.getId() + 1)
                    .file(request)
                    .file(postImg)
                    .header(TOKEN_HEADER, TOKEN_PREFIX + token)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .with(makeRequestMethodToPut());

                // then
                mockMvc.perform(requestBuilder)
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", is(POST_NOT_FOUND)))
                    .andReturn();
            }

            @Test
            @DisplayName("수정할 리소스를 전혀 보내지 않으면 실패한다.")
            void updateFailWithNoResource() throws Exception {
                // given // when
                MockHttpServletRequestBuilder requestBuilder = multipart(BASE_URL + "/" + userPost.getId())
                    .header(TOKEN_HEADER, TOKEN_PREFIX + token)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .with(makeRequestMethodToPut());

                // then
                mockMvc.perform(requestBuilder)
                    .andExpect(status().isBadRequest())
                    .andReturn();
            }

            @Test
            @DisplayName("수정할 이미지만 보내면 수정에 실패한다.")
            void updateFailWithNullImage() throws Exception {
                // given
                MockMultipartFile postImg = getPostImg(ORIGINAL_FILE_NAME, MediaType.IMAGE_JPEG_VALUE);

                // when
                MockHttpServletRequestBuilder requestBuilder = multipart(BASE_URL + "/" + userPost.getId())
                    .file(postImg)
                    .header(TOKEN_HEADER, TOKEN_PREFIX + token)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .with(makeRequestMethodToPut());

                // then
                mockMvc.perform(requestBuilder)
                    .andExpect(status().isBadRequest())
                    .andReturn();
            }

            @ParameterizedTest(name = "[{index}] 제목이 {0}이고 내용이 {1}인 경우")
            @MethodSource("provideInvalidPostInfo")
            @DisplayName("수정할 게시글 정보를 보냈는데 유효하지 않으면 수정에 실패한다.")
            void updateFailWithInvalidPostUpdateRequest(String title, String content) throws Exception {
                // given
                MockMultipartFile request = getRequestJson(title, content);
                MockMultipartFile postImg = getPostImg(ORIGINAL_FILE_NAME, MediaType.IMAGE_JPEG_VALUE);

                // when
                MockHttpServletRequestBuilder requestBuilder = multipart(BASE_URL + "/" + userPost.getId())
                    .file(request)
                    .file(postImg)
                    .header(TOKEN_HEADER, TOKEN_PREFIX + token)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .with(makeRequestMethodToPut());

                // then
                mockMvc.perform(requestBuilder)
                    .andExpect(status().isBadRequest())
                    .andReturn();
            }

            @ParameterizedTest(name = "[{index}] 파일 타입이 {1}인 경우")
            @MethodSource("provideInvalidFile")
            @DisplayName("수정할 게시글 정보와 파일을 보냈는데 파일이 유효하지 않으면 수정에 실패한다.")
            void updateFailWithInvalidFileType(String originalFileName, String mediaType) throws Exception {
                // given
                MockMultipartFile request = getRequestJson(TITLE, CONTENT);
                MockMultipartFile postImg = getPostImg(originalFileName, mediaType);

                // when
                MockHttpServletRequestBuilder requestBuilder = multipart(BASE_URL + "/" + userPost.getId())
                    .file(request)
                    .file(postImg)
                    .header(TOKEN_HEADER, TOKEN_PREFIX + token)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .with(makeRequestMethodToPut());

                // then
                mockMvc.perform(requestBuilder)
                    .andExpect(status().isBadRequest())
                    .andReturn();
            }

        }

    }

    @Nested
    @DisplayName("게시글 저장하기")
    class SaveTest {

        static Stream<Arguments> provideInvalidPostInfo() {
            return Stream.of(
                Arguments.of(null, CONTENT),
                Arguments.of(PostControllerTest.TITLE, null),
                Arguments.of("", CONTENT),
                Arguments.of(PostControllerTest.TITLE, ""),
                Arguments.of(" ", CONTENT),
                Arguments.of(PostControllerTest.TITLE, " "),
                Arguments.of("a".repeat(40), CONTENT),
                Arguments.of(PostControllerTest.TITLE, "a".repeat(600))
            );
        }

        static Stream<Arguments> provideImageTypes() {
            return Stream.of(
                Arguments.of("image.jpeg", MediaType.IMAGE_JPEG_VALUE),
                Arguments.of("image.gif", MediaType.IMAGE_GIF_VALUE),
                Arguments.of("image.png", MediaType.IMAGE_PNG_VALUE)
            );
        }

        static Stream<Arguments> provideInvalidFile() {
            return Stream.of(
                Arguments.of("file.md", MediaType.TEXT_MARKDOWN_VALUE),
                Arguments.of("file.html", MediaType.TEXT_HTML_VALUE),
                Arguments.of("file.pdf", MediaType.APPLICATION_PDF_VALUE),
                Arguments.of("file.txt", MediaType.TEXT_PLAIN_VALUE)
            );
        }

        @ParameterizedTest(name = "[{index}]: 아이템 타입이 {0}인 경우에 저장에 성공한다.")
        @MethodSource("provideImageTypes")
        @DisplayName("게시글 저장에 성공한다.")
        void saveSuccess(String originalFileName, String mediaType) throws Exception {
            // given
            given(weatherService.getCurrentTemperatureArrange(VALID_COORDINATE))
                .willReturn(generateTemperatureArrange());

            MockMultipartFile request = getRequestJson(TITLE, CONTENT);
            MockMultipartFile postImg = getPostImg(originalFileName, mediaType);

            // when
            MockHttpServletRequestBuilder requestBuilder = multipart(BASE_URL)
                .file(request)
                .file(postImg)
                .header(TOKEN_HEADER, TOKEN_PREFIX + token)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8);

            // then
            MockHttpServletResponse response = mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

            assertThat(response.getHeader("location")).contains("/api/v1/posts/");
        }

        @Test
        @DisplayName("저장되지 않은 유저가 포함된 게시글 저장에 실패한다.")
        void saveFailNonSavedUser() throws Exception {
            // given
            setToken(user.getId() + 1);

            given(weatherService.getCurrentTemperatureArrange(VALID_COORDINATE))
                .willReturn(generateTemperatureArrange());

            MockMultipartFile request = getRequestJson(TITLE, CONTENT);
            MockMultipartFile postImg = getPostImg(ORIGINAL_FILE_NAME, MediaType.IMAGE_JPEG_VALUE);

            // when
            MockHttpServletRequestBuilder requestBuilder = multipart(BASE_URL)
                .file(request)
                .file(postImg)
                .header(TOKEN_HEADER, TOKEN_PREFIX + token)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON);

            // then
            mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @ParameterizedTest(name = "[{index}] 제목이 {0}이고 내용이 {1}인 경우")
        @MethodSource("provideInvalidPostInfo")
        @DisplayName("유효하지 않은 요청 값이 포함된 게시글 저장에 실패한다.")
        void saveFailByInvalidPostSaveRequest(String title, String content) throws Exception {
            // given
            MockMultipartFile request = getRequestJson(title, content);
            MockMultipartFile postImg = getPostImg(ORIGINAL_FILE_NAME, MediaType.IMAGE_JPEG_VALUE);

            // when
            MockHttpServletRequestBuilder requestBuilder = multipart(BASE_URL)
                .file(request)
                .file(postImg)
                .header(TOKEN_HEADER, TOKEN_PREFIX + token)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON);

            // then
            mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @ParameterizedTest(name = "[{index}] 파일 타입이 {1}인 경우")
        @MethodSource("provideInvalidFile")
        @DisplayName("게시글 정보와 파일을 보냈는데 파일이 유효하지 않으면 저장에 실패한다.")
        void saveFailByInvalidFileType(String originalFileName, String mediaType) throws Exception {
            // given
            MockMultipartFile request = getRequestJson(TITLE, CONTENT);
            MockMultipartFile postImg = getPostImg(originalFileName, mediaType);

            // when
            MockHttpServletRequestBuilder requestBuilder = multipart(BASE_URL)
                .file(request)
                .file(postImg)
                .header(TOKEN_HEADER, TOKEN_PREFIX + token)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON);

            // then
            mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }
    }

    @Nested
    @DisplayName("게시글 단건 조회하기")
    class GetDetailByPostIdTest {

        private static final String URL = "http://localhost:8080/api/v1/posts/";

        PostSaveUpdateResponse postSaveResponse;

        @BeforeEach
        void setUp() {
            TestSecurityContextHolder.setAuthentication(new TestingAuthenticationToken(user.getId(), null));

            Post savedPost = postRepository.save(
                Post.from(user, new PostSaveRequest(TITLE, CONTENT, VALID_COORDINATE), IMG_URL,
                    generateTemperatureArrange()));
            postSaveResponse = PostSaveUpdateResponse.from(savedPost);
        }

        @Test
        @DisplayName("존재하지 않는 게시글 단건 조회에 실패한다.")
        void getDetailByPostIdFailNonSavedPost() throws Exception {
            // given // when
            MockHttpServletRequestBuilder requestBuilder = get(URL + postSaveResponse.postId() + 1)
                .header(TOKEN_HEADER, TOKEN_PREFIX + token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

            // then
            mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        @DisplayName("게시글 단건 조회에 성공한다.")
        void getDetailByPostIdSuccess() throws Exception {
            // given // when
            MockHttpServletRequestBuilder requestBuilder = get(URL + postSaveResponse.postId())
                .header(TOKEN_HEADER, TOKEN_PREFIX + token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

            // then
            mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

    }

    @Nested
    @DisplayName("게시글 목록 조회하기")
    class GetAllTest {

        static final Integer SAVE_COUNT = 10;

        @BeforeEach
        void setUp() {
            TestSecurityContextHolder.setAuthentication(new TestingAuthenticationToken(user.getId(), null));

            for (int i = 0; i < SAVE_COUNT; i++) {
                postRepository.save(Post.from(user, new PostSaveRequest(TITLE, CONTENT, VALID_COORDINATE), IMG_URL,
                        generateTemperatureArrange()));
            }
        }

        @Test
        @DisplayName("게시글 목록 조회에 성공한다.")
        void getAllSuccess() throws Exception {
            // given // when
            MockHttpServletRequestBuilder requestBuilder = get(BASE_URL)
                .header(TOKEN_HEADER, TOKEN_PREFIX + token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

            // then
            String response = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

            List<PostReadResponse> posts = objectMapper.readValue(response, List.class);

            assertThat(posts.size()).isEqualTo(SAVE_COUNT);
        }

    }


    private TemperatureArrange generateTemperatureArrange() {
        Map<ForecastCategory, String> weatherInfoMap = new HashMap<>();
        weatherInfoMap.put(ForecastCategory.TMN, String.valueOf(0.0));
        weatherInfoMap.put(ForecastCategory.TMX, String.valueOf(15.0));

        return TemperatureArrange.from(weatherInfoMap);
    }

}
