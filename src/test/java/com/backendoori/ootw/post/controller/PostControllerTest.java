package com.backendoori.ootw.post.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.List;
import com.backendoori.ootw.exception.ExceptionResponse;
import com.backendoori.ootw.exception.ExceptionResponse.FieldErrorDetail;
import com.backendoori.ootw.post.dto.PostReadResponse;
import com.backendoori.ootw.post.dto.PostSaveRequest;
import com.backendoori.ootw.post.dto.PostSaveResponse;
import com.backendoori.ootw.post.dto.WeatherDto;
import com.backendoori.ootw.post.repository.PostRepository;
import com.backendoori.ootw.post.service.PostService;
import com.backendoori.ootw.user.domain.User;
import com.backendoori.ootw.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {

    User savedUser;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PostController postController;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User("user@email.com", "password", "nickname", null);
        savedUser = userRepository.save(user);
    }

    @Nested
    @DisplayName("게시글 저장하기")
    class Save {

        @Test
        @WithMockUser
        @DisplayName("게시글 저장에 성공한다.")
        void saveSuccess() throws Exception {
            // given
            WeatherDto weatherDto =
                new WeatherDto(0.0, -10.0, 10.0, 1, 1);
            PostSaveRequest postSaveRequest =
                new PostSaveRequest(savedUser.getId(), "Test Title", "Test Content", weatherDto);
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
        @WithMockUser
        @DisplayName("저장되지 않은 유저가 포함된 게시글 저장에 실패한다.")
        void saveFailNonSavedUser() throws Exception {
            // given
            WeatherDto weatherDto =
                new WeatherDto(0.0, -10.0, 10.0, 1, 1);
            PostSaveRequest postSaveRequest =
                new PostSaveRequest(savedUser.getId() + 1, "Test Title", "Test Content", weatherDto);
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
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON);

            // then
            mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        @WithMockUser
        @DisplayName("유효하지 않은 요청 값(게시글 title)이 포함된 게시글 저장에 실패한다.")
        void saveFailByMethodArgumentNotValidException() throws Exception {
            // given
            WeatherDto weatherDto =
                new WeatherDto(0.0, -10.0, 10.0, 1, 1);
            PostSaveRequest postSaveRequest =
                new PostSaveRequest(savedUser.getId(), "", "Test Content", weatherDto);
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
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON);

            // then
            String response = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

            ExceptionResponse<List<FieldErrorDetail>> exceptionResponse =
                objectMapper.readValue(response, ExceptionResponse.class);
            assertThat(exceptionResponse.error()).hasSize(1);
        }

        @Test
        @WithMockUser
        @DisplayName("유효하지 않은 요청 값(현재 기온)이 포함된 게시글 저장에 실패한다.")
        void saveFailInvalidValueByIllegalArgumentException() throws Exception {
            // given
            WeatherDto weatherDto =
                new WeatherDto(900.0, -10.0, 10.0, 1, 1);
            PostSaveRequest postSaveRequest =
                new PostSaveRequest(savedUser.getId(), "", "Test Content", weatherDto);
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
    class GetDatailByPostId {

        PostSaveResponse savedPost;

        @BeforeEach
        void setUp() {
            WeatherDto weatherDto =
                new WeatherDto(0.0, -10.0, 10.0, 1, 1);
            MockMultipartFile postImg = new MockMultipartFile("file", "filename.txt",
                "text/plain", "some xml".getBytes());
            savedPost = postService.save(
                new PostSaveRequest(savedUser.getId(), "Test Title", "Test Content", weatherDto), postImg);
        }

        @Test
        @WithMockUser
        @DisplayName("존재하지 않는 게시글 단건 조회에 실패한다.")
        void getDetailByPostIdFailNonSavedPost() throws Exception {
            // given, when, then
            mockMvc.perform(get("http://localhost:8080/api/v1/posts/" + savedPost.postId() + 1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        @WithMockUser
        @DisplayName("게시글 단건 조회에 성공한다.")
        void getDetailByPostIdSuccess() throws Exception {
            // given, when, then
            mockMvc.perform(get("http://localhost:8080/api/v1/posts/" + savedPost.postId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
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

            WeatherDto weatherDto =
                new WeatherDto(0.0, -10.0, 10.0, 1, 1);
            PostSaveRequest request =
                new PostSaveRequest(savedUser.getId(), "Test Title", "Test Content", weatherDto);
            MockMultipartFile postImg = new MockMultipartFile("file", "filename.txt",
                "text/plain", "some xml".getBytes());

            for (int i = 0; i < SAVE_COUNT; i++) {
                postService.save(request, postImg);
            }
        }

        @Test
        @WithMockUser
        @DisplayName("게시글 목록 조회 성공")
        void getAllSuccess() throws Exception {
            // given, when, then
            String response = mockMvc.perform(get("http://localhost:8080/api/v1/posts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

            List<PostReadResponse> posts = objectMapper.readValue(response, List.class);
            assertThat(posts.size()).isEqualTo(SAVE_COUNT);

        }

    }

}
