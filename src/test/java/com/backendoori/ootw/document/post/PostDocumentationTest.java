package com.backendoori.ootw.document.post;

import static com.backendoori.ootw.document.common.ApiDocumentUtil.field;
import static com.backendoori.ootw.document.common.ApiDocumentUtil.getDocumentRequest;
import static com.backendoori.ootw.document.common.ApiDocumentUtil.getDocumentResponse;
import static com.backendoori.ootw.security.jwt.JwtAuthenticationFilter.TOKEN_HEADER;
import static com.backendoori.ootw.security.jwt.JwtAuthenticationFilter.TOKEN_PREFIX;
import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.VALID_COORDINATE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.backendoori.ootw.post.dto.request.PostSaveRequest;
import com.backendoori.ootw.post.dto.request.PostUpdateRequest;
import com.backendoori.ootw.post.dto.response.PostReadResponse;
import com.backendoori.ootw.post.dto.response.PostSaveUpdateResponse;
import com.backendoori.ootw.post.dto.response.WriterDto;
import com.backendoori.ootw.post.service.PostService;
import com.backendoori.ootw.security.TokenMockMvcTest;
import com.backendoori.ootw.weather.domain.TemperatureArrange;
import com.backendoori.ootw.weather.domain.forecast.ForecastCategory;
import com.backendoori.ootw.weather.dto.TemperatureArrangeDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@SpringBootTest
@AutoConfigureRestDocs
class PostDocumentationTest extends TokenMockMvcTest {

    static final String API_PREFIX = "/api/v1/posts";
    static final Faker FAKER = new Faker();

    @MockBean
    PostService postService;

    @DisplayName("[POST] save 201 Created")
    @Test
    void testSaveCreated() throws Exception {
        // given
        PostSaveRequest postSaveRequest =
            new PostSaveRequest(FAKER.book().title(), FAKER.science().element(), VALID_COORDINATE);
        MockMultipartFile request = getRequestJson(postSaveRequest);
        MockMultipartFile postImg = getPostImg();

        setToken(1);
        given(postService.save(postSaveRequest, postImg))
            .willReturn(generatePostSaveUpdateResponse(1L, postSaveRequest.title(), postSaveRequest.content()));

        // when
        ResultActions actions = mockMvc.perform(multipart(API_PREFIX)
            .file(request)
            .file(postImg)
            .header(TOKEN_HEADER, TOKEN_PREFIX + token)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .accept(MediaType.APPLICATION_JSON)
            .characterEncoding(StandardCharsets.UTF_8)
        );

        // then
        actions.andExpect(status().isCreated())
            .andDo(
                document("post-create",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 토큰")
                    ),
                    requestParts(
                        partWithName("request").description("게시글 생성 요청 정보"),
                        partWithName("postImg").description("게시글 이미지 파일")
                    ),
                    requestPartFields(
                        "request",
                        fieldWithPath("title").description("게시글 제목"),
                        fieldWithPath("content").description("게시글 내용"),
                        fieldWithPath("coordinate.nx").description("사용자 X 좌표"),
                        fieldWithPath("coordinate.ny").description("사용자 Y 좌표")
                    ),
                    responseFields(
                        field("postId", JsonFieldType.NUMBER, "게시글 ID"),
                        field("title", JsonFieldType.STRING, "게시글 제목"),
                        field("content", JsonFieldType.STRING, "게시글 내용"),
                        field("image", JsonFieldType.STRING, "게시글 이미지 URL"),
                        field("createdAt", JsonFieldType.STRING, "게시글 생성 일자"),
                        field("updatedAt", JsonFieldType.STRING, "게시글 수정 일자"),
                        field("temperatureArrange.min", JsonFieldType.NUMBER, "최저 기온"),
                        field("temperatureArrange.max", JsonFieldType.NUMBER, "최고 기온")
                    )
                )
            );
    }

    @DisplayName("[GET] readDetailByPostId 200 Ok")
    @Test
    void testReadDetailByPostIdOk() throws Exception {
        // given
        long postId = FAKER.number().positive();

        setToken(1);
        given(postService.getDetailByPostId(postId))
            .willReturn(generatePostReadResponse(postId));

        // when
        ResultActions actions = mockMvc.perform(get(API_PREFIX + "/{postId}", postId)
            .header(TOKEN_HEADER, TOKEN_PREFIX + token)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        );

        // then
        actions.andExpect(status().isOk())
            .andDo(
                document("post-read-detail",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 토큰")
                    ),
                    pathParameters(
                        parameterWithName("postId").description("게시글 ID")
                    ),
                    responseFields(
                        field("postId", JsonFieldType.NUMBER, "게시글 ID"),
                        field("writer.userId", JsonFieldType.NUMBER, "게시글 작성자 ID"),
                        field("writer.nickname", JsonFieldType.STRING, "게시글 작성자 별명"),
                        field("writer.image", JsonFieldType.STRING, "게시글 작성자 프로필 이미지 URL"),
                        field("title", JsonFieldType.STRING, "게시글 제목"),
                        field("content", JsonFieldType.STRING, "게시글 내용"),
                        field("image", JsonFieldType.STRING, "게시글 이미지 URL"),
                        field("createdAt", JsonFieldType.STRING, "게시글 생성 일자"),
                        field("updatedAt", JsonFieldType.STRING, "게시글 수정 일자"),
                        field("temperatureArrange.min", JsonFieldType.NUMBER, "최저 기온"),
                        field("temperatureArrange.max", JsonFieldType.NUMBER, "최고 기온"),
                        field("likeCnt", JsonFieldType.NUMBER, "좋아요 개수"),
                        field("isLike", JsonFieldType.NUMBER, "좋아요 여부")
                    )
                )
            );
    }

    @DisplayName("[GET] readAll 200 Ok")
    @Test
    void testReadAllOk() throws Exception {
        // given
        setToken(1);
        given(postService.getAll())
            .willReturn(List.of(generatePostReadResponse(FAKER.number().positive()),
                generatePostReadResponse(FAKER.number().positive()),
                generatePostReadResponse(FAKER.number().positive())
            ));

        // when
        ResultActions actions = mockMvc.perform(get(API_PREFIX)
            .header(TOKEN_HEADER, TOKEN_PREFIX + token)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        );

        // then
        actions.andExpect(status().isOk())
            .andDo(
                document("post-read-all",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 토큰")
                    ),
                    responseFields(
                        field("[]postId", JsonFieldType.NUMBER, "게시글 ID"),
                        field("[]writer.userId", JsonFieldType.NUMBER, "게시글 작성자 ID"),
                        field("[]writer.nickname", JsonFieldType.STRING, "게시글 작성자 별명"),
                        field("[]writer.image", JsonFieldType.STRING, "게시글 작성자 프로필 이미지 URL"),
                        field("[]title", JsonFieldType.STRING, "게시글 제목"),
                        field("[]content", JsonFieldType.STRING, "게시글 내용"),
                        field("[]image", JsonFieldType.STRING, "게시글 이미지 URL"),
                        field("[]createdAt", JsonFieldType.STRING, "게시글 생성 일자"),
                        field("[]updatedAt", JsonFieldType.STRING, "게시글 수정 일자"),
                        field("[]temperatureArrange.min", JsonFieldType.NUMBER, "최저 기온"),
                        field("[]temperatureArrange.max", JsonFieldType.NUMBER, "최고 기온"),
                        field("[]likeCnt", JsonFieldType.NUMBER, "좋아요 개수"),
                        field("[]isLike", JsonFieldType.NUMBER, "좋아요 여부")
                    )
                )
            );
    }

    @DisplayName("[PUT] update 201 Created")
    @Test
    void testUpdateCreated() throws Exception {
        // given
        long postId = 2;
        PostUpdateRequest postUpdateRequest =
            new PostUpdateRequest(FAKER.book().title(), FAKER.science().element());
        MockMultipartFile request = getRequestJson(postUpdateRequest);

        setToken(1);
        given(postService.update(any(), any(), any()))
            .willReturn(generatePostSaveUpdateResponse(postId, postUpdateRequest.title(), postUpdateRequest.content()));

        // when
        ResultActions actions =
            mockMvc.perform(multipart(API_PREFIX + "/{postId}", postId)
                .file(request)
                .header(TOKEN_HEADER, TOKEN_PREFIX + token)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .with(setMethod("PUT"))
            );

        // then
        actions.andExpect(status().isCreated())
            .andDo(
                document("post-update",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 토큰")
                    ),
                    pathParameters(
                        parameterWithName("postId").description("게시글 ID")
                    ),
                    requestParts(
                        partWithName("request").description("게시글 생성 요청 정보")
                    ),
                    requestPartFields(
                        "request",
                        fieldWithPath("title").description("게시글 제목"),
                        fieldWithPath("content").description("게시글 내용")
                    ),
                    responseFields(
                        field("postId", JsonFieldType.NUMBER, "게시글 ID"),
                        field("title", JsonFieldType.STRING, "게시글 제목"),
                        field("content", JsonFieldType.STRING, "게시글 내용"),
                        field("image", JsonFieldType.STRING, "게시글 이미지 URL"),
                        field("createdAt", JsonFieldType.STRING, "게시글 생성 일자"),
                        field("updatedAt", JsonFieldType.STRING, "게시글 수정 일자"),
                        field("temperatureArrange.min", JsonFieldType.NUMBER, "최저 기온"),
                        field("temperatureArrange.max", JsonFieldType.NUMBER, "최고 기온")
                    )
                )
            );
    }

    @DisplayName("[DELETE] delete 204 NoContent")
    @Test
    void testDeleteNoContent() throws Exception {
        // given
        long postId = FAKER.number().positive();

        setToken(1);

        // when
        ResultActions actions = mockMvc.perform(delete(API_PREFIX + "/{postId}", postId)
            .header(TOKEN_HEADER, TOKEN_PREFIX + token)
        );

        // then
        actions.andExpect(status().isNoContent())
            .andDo(
                document("post-delete",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 토큰")
                    ),
                    pathParameters(
                        parameterWithName("postId").description("게시글 ID")
                    )
                )
            );
    }

    private PostReadResponse generatePostReadResponse(long postId) {
        return new PostReadResponse(postId,
            new WriterDto((long) FAKER.number().positive(), FAKER.internet().username(), FAKER.internet().url()),
            FAKER.book().title(), FAKER.science().element(), FAKER.internet().url(), LocalDateTime.now(),
            LocalDateTime.now(), TemperatureArrangeDto.from(generateTemperatureArrange()),
            FAKER.number().numberBetween(1, 100), FAKER.number().numberBetween(0, 1));
    }

    private static TemperatureArrange generateTemperatureArrange() {
        Map<ForecastCategory, String> weatherInfoMap = new HashMap<>();
        weatherInfoMap.put(ForecastCategory.TMN, String.valueOf(0.0));
        weatherInfoMap.put(ForecastCategory.TMX, String.valueOf(15.0));

        return TemperatureArrange.from(weatherInfoMap);
    }

    private MockMultipartFile getRequestJson(PostSaveRequest postSaveRequest) throws JsonProcessingException {
        return new MockMultipartFile("request", "request.json", MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(postSaveRequest));
    }

    private MockMultipartFile getRequestJson(PostUpdateRequest postUpdateRequest) throws JsonProcessingException {
        return new MockMultipartFile("request", "request.json", MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(postUpdateRequest));
    }

    private MockMultipartFile getPostImg() {
        return new MockMultipartFile("postImg", "image.jpeg", MediaType.IMAGE_JPEG_VALUE, "content".getBytes());
    }

    private RequestPostProcessor setMethod(String method) {
        return req -> {
            req.setMethod(method);

            return req;
        };
    }

    private PostSaveUpdateResponse generatePostSaveUpdateResponse(Long postId, String title, String content) {
        return new PostSaveUpdateResponse(postId, title, content, FAKER.internet().url(), LocalDateTime.now(),
            LocalDateTime.now(), TemperatureArrangeDto.from(generateTemperatureArrange()));
    }

}
