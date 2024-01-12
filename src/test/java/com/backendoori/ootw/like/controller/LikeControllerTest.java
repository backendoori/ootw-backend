package com.backendoori.ootw.like.controller;

import static com.backendoori.ootw.security.jwt.JwtAuthenticationFilter.TOKEN_HEADER;
import static com.backendoori.ootw.security.jwt.JwtAuthenticationFilter.TOKEN_PREFIX;
import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.VALID_COORDINATE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;
import com.backendoori.ootw.like.domain.Like;
import com.backendoori.ootw.like.dto.controller.LikeRequest;
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
import jakarta.transaction.Transactional;
import net.datafaker.Faker;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class LikeControllerTest extends TokenMockMvcTest {

    static final Faker FAKER = new Faker();
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
            .profileImageUrl(FAKER.internet().url())
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
    @Transactional
    @DisplayName("정상적으로 좋아요를 누르면 저장에 성공한다.")
    public void likeSuccess() throws Exception {
        //given
        LikeRequest request = new LikeRequest(post.getId());

        //when
        mockMvc.perform(post("http://localhost:8080/api/v1/posts/" + post.getId() + "/likes")
                .content(objectMapper.writeValueAsBytes(request))
                .header(TOKEN_HEADER, TOKEN_PREFIX + token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk())
            .andDo(print());

        //then
        Like byUserAndPost = likeRepository.findByUserAndPost(user, post).get();

        Assertions.assertThat(byUserAndPost.getPost()).isEqualTo(post);
        Assertions.assertThat(byUserAndPost.getUser()).isEqualTo(user);
        Assertions.assertThat(byUserAndPost.getIsLike()).isEqualTo(true);

    }

    @Test
    @Transactional
    @DisplayName("이미 좋아요를 누른 경우 좋아요가 취소된다.")
    public void likeCancel() throws Exception {
        //given
        Like like = Like.builder().user(user).post(post).isLike(true).build();
        likeRepository.save(like);

        LikeRequest request = new LikeRequest(post.getId());

        //when
        mockMvc.perform(post("http://localhost:8080/api/v1/posts/" + post.getId() + "/likes")
                .content(objectMapper.writeValueAsBytes(request))
                .header(TOKEN_HEADER, TOKEN_PREFIX + token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk())
            .andDo(print());

        //then
        Like byUserAndPost = likeRepository.findByUserAndPost(user, post).get();

        Assertions.assertThat(byUserAndPost.getPost()).isEqualTo(post);
        Assertions.assertThat(byUserAndPost.getUser()).isEqualTo(user);
        Assertions.assertThat(byUserAndPost.getIsLike()).isEqualTo(false);

    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0})
    @DisplayName("유효하지 않은 postId 로 좋아요를 요청하면 실패한다.")
    public void likeFailPostNotFound(Long postId) throws Exception {
        //given
        LikeRequest request = new LikeRequest(postId);

        //when //then
        mockMvc.perform(post("http://localhost:8080/api/v1/posts/" + postId + "/likes")
                .content(objectMapper.writeValueAsBytes(request))
                .header(TOKEN_HEADER, TOKEN_PREFIX + token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
            ).andExpect(status().isNotFound())
            .andDo(print());

    }

    @ParameterizedTest
    @ValueSource(longs = Long.MAX_VALUE)
    @DisplayName("존재하지 않는 post 에 좋아요를 요청하면 실패한다.")
    public void likeFailNullPostId(Long postId) throws Exception {
        //given
        LikeRequest request = new LikeRequest(postId);

        //when //then
        mockMvc.perform(post("http://localhost:8080/api/v1/posts/" + postId + "/likes")
                .content(objectMapper.writeValueAsBytes(request))
                .header(TOKEN_HEADER, TOKEN_PREFIX + token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
            ).andExpect(status().isNotFound())
            .andDo(print());

    }

}
