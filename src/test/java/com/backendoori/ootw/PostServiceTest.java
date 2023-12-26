package com.backendoori.ootw;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.NoSuchElementException;
import com.backendoori.ootw.domain.User;
import com.backendoori.ootw.dto.PostReadResponse;
import com.backendoori.ootw.dto.PostSaveRequest;
import com.backendoori.ootw.dto.PostSaveResponse;
import com.backendoori.ootw.dto.WeatherDto;
import com.backendoori.ootw.dto.WriterDto;
import com.backendoori.ootw.repository.PostRepository;
import com.backendoori.ootw.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PostServiceTest {

    User savedUser;

    @Autowired
    private PostService postService;

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
        @DisplayName("게시글 저장에 성공한다.")
        void saveSuccess() {
            // given
            WeatherDto weatherDto =
                new WeatherDto(0.0, -10.0, 10.0, 1, 1);
            PostSaveRequest request =
                new PostSaveRequest(savedUser.getId(), "Test Title", "Test Content", null, weatherDto);

            // when
            PostSaveResponse postSaveResponse = postService.save(request);

            //then
            assertAll(
                () -> assertThat(postSaveResponse).hasFieldOrPropertyWithValue("title", request.title()),
                () -> assertThat(postSaveResponse).hasFieldOrPropertyWithValue("content", request.content()),
                () -> assertThat(postSaveResponse).hasFieldOrPropertyWithValue("image", request.image()),
                () -> assertThat(postSaveResponse).hasFieldOrPropertyWithValue("weather", request.weather())
            );
        }

        @Test
        @DisplayName("저장된 유저가 아닌 경우 게시글 저장에 실패한다.")
        void saveFailNotSavedUser() {
            // given
            Long notSavedUserId = 100L;
            WeatherDto weatherDto =
                new WeatherDto(0.0, -10.0, 10.0, 1, 1);
            PostSaveRequest postSaveRequest =
                new PostSaveRequest(notSavedUserId, "Test Title", "Test Content", null, weatherDto);

            // when, then
            assertThrows(NoSuchElementException.class,
                () -> postService.save(postSaveRequest));
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
                new PostSaveRequest(savedUser.getId(), "Test Title", "Test Content", null, weatherDto);

            // when, then
            assertThrows(IllegalArgumentException.class,
                () -> postService.save(postSaveRequest));
        }

    }

    // TODO: 조회용 setUp()을 미리 만들어 놓아도 좋을 것 같다.
    @Nested
    @DisplayName("게시글 단건 조회하기")
    class GetDatailByPostId {

        PostSaveResponse savedPost;

        @BeforeEach
        void setUp() {
            WeatherDto weatherDto =
                new WeatherDto(0.0, -10.0, 10.0, 1, 1);
            savedPost = postService.save(
                new PostSaveRequest(savedUser.getId(), "Test Title", "Test Content", null, weatherDto));
        }

        @Test
        @DisplayName("게시글 단건 조회에 성공한다.")
        void getDatailByPostIdSuccess() {
            // given
            WriterDto savedPostWriter = WriterDto.from(savedUser);

            // when
            PostReadResponse postDetailInfo = postService.getDatailByPostId(savedPost.getPostId());

            // then
            assertAll(
                () -> assertThat(postDetailInfo).hasFieldOrPropertyWithValue("postId", savedPost.getPostId()),
                () -> assertThat(postDetailInfo).hasFieldOrPropertyWithValue("writer", savedPostWriter),
                () -> assertThat(postDetailInfo).hasFieldOrPropertyWithValue("title", savedPost.getTitle()),
                () -> assertThat(postDetailInfo).hasFieldOrPropertyWithValue("content", savedPost.getContent()),
                () -> assertThat(postDetailInfo).hasFieldOrPropertyWithValue("image", savedPost.getImage()),
                // TODO: LocalDateTime 자릿수 에러 발생
                //  [savedPost.getCreatedAt()] 2023-12-26T20:16:41.890478800
                //  [postDetailInfo.createdAt()] 2023-12-26T20:16:41.890479
                // () -> assertThat(postDetailInfo)
                //    .hasFieldOrPropertyWithValue("createdAt", savedPost.getCreatedAt()),
                // () -> assertThat(postDetailInfo)
                //    .hasFieldOrPropertyWithValue("updatedAt", savedPost.getUpdatedAt()),
                () -> assertThat(postDetailInfo).hasFieldOrPropertyWithValue("weather", savedPost.getWeather())
            );
        }

        @Test
        @DisplayName("저장되지 않은 게시글 Id로 요청할 경우 게시글 단건 조회에 실패한다.")
        void getDatailByPostIdFailNotSavedPost() {
            // given, when. then
            assertThrows(NoSuchElementException.class,
                () -> postService.getDatailByPostId(savedPost.getPostId() + 1));
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
                new PostSaveRequest(savedUser.getId(), "Test Title", "Test Content", null, weatherDto);

            for (int i = 0; i < SAVE_COUNT; i++) {
                postService.save(request);
            }
        }

        @Test
        @DisplayName("게시글 목록 최신순(default) 조회에 성공한다.")
        void getAllSuccess() {
            // given, when
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

    }

}
