package com.backendoori.ootw.post.controller;

import static com.backendoori.ootw.security.jwt.JwtAuthenticationFilter.TOKEN_HEADER;
import static com.backendoori.ootw.security.jwt.JwtAuthenticationFilter.TOKEN_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.backendoori.ootw.post.domain.Post;
import com.backendoori.ootw.post.dto.PostReadResponse;
import com.backendoori.ootw.post.dto.PostSaveRequest;
import com.backendoori.ootw.post.dto.PostSaveResponse;
import com.backendoori.ootw.post.repository.PostRepository;
import com.backendoori.ootw.post.service.PostService;
import com.backendoori.ootw.security.TokenMockMvcTest;
import com.backendoori.ootw.user.domain.User;
import com.backendoori.ootw.user.repository.UserRepository;
import com.backendoori.ootw.weather.domain.TemperatureArrange;
import com.backendoori.ootw.weather.domain.forecast.ForecastCategory;
import net.datafaker.Faker;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.test.context.TestSecurityContextHolder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@TestInstance(Lifecycle.PER_CLASS)
class PostControllerTest extends TokenMockMvcTest {

    static final int NX = 55;
    static final int NY = 127;
    static final Faker FAKER = new Faker();

    User user;

    @Autowired
    PostController postController;

    @Autowired
    PostService postService;

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

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
            .image(FAKER.internet().url())
            .build();
    }

    @Nested
    @DisplayName("게시글 저장 테스트")
    class SaveTest {

        @Test
        @DisplayName("게시글 저장에 성공한다.")
        void saveSuccess() throws Exception {
            // given
            PostSaveRequest postSaveRequest =
                new PostSaveRequest("Test Title", "Test Content", NX, NY);
            MockMultipartFile request =
                new MockMultipartFile("request", "request.json", MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(postSaveRequest));
            MockMultipartFile postImg =
                new MockMultipartFile("postImg", "filename.txt", MediaType.MULTIPART_FORM_DATA_VALUE,
                    "some xml".getBytes());

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

            PostSaveRequest postSaveRequest =
                new PostSaveRequest("Test Title", "Test Content", NX, NY);
            MockMultipartFile request =
                new MockMultipartFile("request", "request.json", MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(postSaveRequest));
            MockMultipartFile postImg =
                new MockMultipartFile("postImg", "filename.txt", MediaType.MULTIPART_FORM_DATA_VALUE,
                    "some xml".getBytes());

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

        @Test
        @DisplayName("유효하지 않은 요청 값(게시글 title)이 포함된 게시글 저장에 실패한다.")
        void saveFailByMethodArgumentNotValidException() throws Exception {
            // given
            PostSaveRequest postSaveRequest = new PostSaveRequest("", "Test Content", NX, NY);
            MockMultipartFile request =
                new MockMultipartFile("request", "request.json", MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(postSaveRequest));
            MockMultipartFile postImg =
                new MockMultipartFile("postImg", "filename.txt", MediaType.MULTIPART_FORM_DATA_VALUE,
                    "some xml".getBytes());

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
                .andExpect(jsonPath("$.message", instanceOf(String.class)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        @DisplayName("유효하지 않은 요청 값(최저 기온)이 포함된 게시글 저장에 실패한다.")
        void saveFailInvalidValueByIllegalArgumentException() throws Exception {
            // given
            PostSaveRequest postSaveRequest = new PostSaveRequest("", "Test Content", NX, NY);
            MockMultipartFile request =
                new MockMultipartFile("request", "request.json", MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(postSaveRequest));
            MockMultipartFile postImg =
                new MockMultipartFile("postImg", "filename.txt", MediaType.MULTIPART_FORM_DATA_VALUE,
                    "some xml".getBytes());

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

    }

    @Nested
    @DisplayName("게시글 단건 조회하기")
    class GetDetailByPostId {

        private static final String URL = "http://localhost:8080/api/v1/posts/";

        PostSaveResponse postSaveResponse;

        @BeforeEach
        void setUp() {
            TestSecurityContextHolder.setAuthentication(new TestingAuthenticationToken(user.getId(), null));

            Post savedPost = postRepository.save(
                Post.from(user, new PostSaveRequest("Test Title", "Test Content", NX, NY), "imgUrl",
                    generateTemperatureArrange()));
            postSaveResponse = PostSaveResponse.from(savedPost);
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
    class GetAll {

        static final Integer SAVE_COUNT = 10;

        @BeforeEach
        void setUp() {
            TestSecurityContextHolder.setAuthentication(new TestingAuthenticationToken(user.getId(), null));

            for (int i = 0; i < SAVE_COUNT; i++) {
                Post savedPost = postRepository.save(
                    Post.from(user, new PostSaveRequest("Test Title", "Test Content", NX, NY), "imgUrl",
                        generateTemperatureArrange()));
            }
        }

        @Test
        @DisplayName("게시글 목록 조회 성공")
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
