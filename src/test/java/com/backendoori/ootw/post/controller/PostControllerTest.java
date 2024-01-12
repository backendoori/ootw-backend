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
import com.backendoori.ootw.post.domain.Post;
import com.backendoori.ootw.post.dto.request.PostSaveRequest;
import com.backendoori.ootw.post.dto.response.PostReadResponse;
import com.backendoori.ootw.post.dto.response.PostSaveUpdateResponse;
import com.backendoori.ootw.post.exception.NoPostPermissionException;
import com.backendoori.ootw.post.exception.ResourceNotExistException;
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
    public static final String BASE_URL = "http://localhost:8080/api/v1/posts";

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
    private static MockMultipartFile getPostImg() {
        return new MockMultipartFile("postImg", "filename.jpeg", MediaType.IMAGE_JPEG_VALUE,
            "some xml".getBytes());
    }

    @NotNull
    private static RequestPostProcessor makeRequestMethodToPut() {
        return req -> {
            req.setMethod("PUT");
            return req;
        };
    }

    @NotNull
    private MockMultipartFile getRequestJson(String testTitle) throws JsonProcessingException {
        PostSaveRequest postSaveRequest =
            new PostSaveRequest(testTitle, "Test Content", VALID_COORDINATE);

        return new MockMultipartFile("request", "request.json", MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(postSaveRequest));
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
                .andExpect(jsonPath("$.message", is(NoPostPermissionException.DEFAULT_MESSAGE)))
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
            void updateAllSuccess() throws Exception {
                // given
                MockMultipartFile request = getRequestJson("Test Title");
                MockMultipartFile postImg = getPostImg();

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
                MockMultipartFile request = getRequestJson("Test Title");

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

            @Test
            @DisplayName("로그인을 안한 사용자는 게시글 수정에 접근이 불가하다.")
            void updateFailWithUnauthorizedUser() throws Exception {
                // given
                MockMultipartFile request = getRequestJson("Test Title");
                MockMultipartFile postImg = getPostImg();

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
                MockMultipartFile request = getRequestJson("Test Title");
                MockMultipartFile postImg = getPostImg();

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
                    .andExpect(jsonPath("$.message", is(NoPostPermissionException.DEFAULT_MESSAGE)))
                    .andReturn();
            }

            @Test
            @DisplayName("존재하지 않는 게시글 수정에 실패한다.")
            void updateFailWithNonExistPost() throws Exception {
                // given
                MockMultipartFile request = getRequestJson("Test Title");
                MockMultipartFile postImg = getPostImg();

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
                    .andExpect(jsonPath("$.message", is(ResourceNotExistException.DEFAULT_MESSAGE)))
                    .andReturn();
            }

            @Test
            @DisplayName("수정할 이미지만 보냈는데 null이면 수정에 실패한다.")
            void updateFailWithNullImage() throws Exception {
                // given
                MockMultipartFile request = getRequestJson("Test Title");
                MockMultipartFile postImg = getPostImg();

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
                    .andExpect(status().isCreated())
                    .andReturn();
            }

            @Test
            @DisplayName("수정할 게시글 정보를 보냈는데 null이면 수정에 실패한다.")
            void updateFailWithNullPostUpdateRequest() throws Exception {
                // given
                MockMultipartFile request = getRequestJson("Test Title");
                MockMultipartFile postImg = getPostImg();

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
                    .andExpect(status().isCreated())
                    .andReturn();
            }

        }

    }

    @Nested
    @DisplayName("게시글 저장하기")
    class SaveTest {

        @Test
        @DisplayName("게시글 저장에 성공한다.")
        void saveSuccess() throws Exception {
            // given
            given(weatherService.getCurrentTemperatureArrange(VALID_COORDINATE))
                .willReturn(generateTemperatureArrange());

            MockMultipartFile request = getRequestJson("Test Title");
            MockMultipartFile postImg = getPostImg();

            // when
            MockHttpServletRequestBuilder requestBuilder = multipart("http://localhost:8080/api/v1/posts")
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

            MockMultipartFile request = getRequestJson("Test Title");
            MockMultipartFile postImg = getPostImg();

            // when
            MockHttpServletRequestBuilder requestBuilder = multipart("http://localhost:8080/api/v1/posts")
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
        void saveFailByMethodArgumentNotValidException(String title, String content) throws Exception {
            // given
            MockMultipartFile request = getRequestJson("");
            MockMultipartFile postImg = getPostImg();

            // when
            MockHttpServletRequestBuilder requestBuilder = multipart("http://localhost:8080/api/v1/posts")
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
                Post.from(user, new PostSaveRequest("Test Title", "Test Content", VALID_COORDINATE), "imgUrl",
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
                Post savedPost = postRepository.save(
                    Post.from(user, new PostSaveRequest("Test Title", "Test Content", VALID_COORDINATE), "imgUrl",
                        generateTemperatureArrange()));
            }
        }

        @Test
        @DisplayName("게시글 목록 조회에 성공한다.")
        void getAllSuccess() throws Exception {
            // given // when
            MockHttpServletRequestBuilder requestBuilder = get("http://localhost:8080/api/v1/posts")
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
