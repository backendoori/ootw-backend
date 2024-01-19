package com.backendoori.ootw.document.like;

import static com.backendoori.ootw.document.common.ApiDocumentUtil.field;
import static com.backendoori.ootw.document.common.ApiDocumentUtil.getDocumentRequest;
import static com.backendoori.ootw.document.common.ApiDocumentUtil.getDocumentResponse;
import static com.backendoori.ootw.security.jwt.JwtAuthenticationFilter.TOKEN_HEADER;
import static com.backendoori.ootw.security.jwt.JwtAuthenticationFilter.TOKEN_PREFIX;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backendoori.ootw.like.dto.controller.LikeRequest;
import com.backendoori.ootw.like.dto.controller.LikeResponse;
import com.backendoori.ootw.like.service.LikeService;
import com.backendoori.ootw.security.TokenMockMvcTest;
import com.backendoori.ootw.user.domain.User;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;


@SpringBootTest
@AutoConfigureRestDocs
class LikeDocumentationTest extends TokenMockMvcTest {

    static final String API_PREFIX = "/api/v1/posts";
    static final Faker FAKER = new Faker();

    @MockBean
    LikeService likeService;


    @DisplayName("[POST] pushLike 200 Ok")
    @Test
    void testPushLikeOk() throws Exception {
        // given
        User user = generateUser();
        long postId = FAKER.number().positive();
        LikeRequest request = new LikeRequest(postId);

        setToken(user.getId());
        given(likeService.requestLike(user.getId(), postId))
            .willReturn(new LikeResponse((long) FAKER.number().positive(), user.getId(), postId, true));

        // when
        ResultActions actions = mockMvc.perform(post(API_PREFIX + "/" + postId + "/likes")
            .header(TOKEN_HEADER, TOKEN_PREFIX + token)
            .content(objectMapper.writeValueAsBytes(request))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        );

        // then
        actions.andExpect(status().isOk())
            .andDo(
                document("like-push",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 토큰")
                    ),
                    requestFields(
                        fieldWithPath("postId").description("게시글 ID")
                    ),
                    responseFields(
                        field("likeId", JsonFieldType.NUMBER, "좋아요 ID"),
                        field("userId", JsonFieldType.NUMBER, "좋아요를 누른 사용자 ID"),
                        field("postId", JsonFieldType.NUMBER, "게시글 ID"),
                        field("status", JsonFieldType.BOOLEAN, "좋아요 여부")
                    )
                )
            );
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

}
