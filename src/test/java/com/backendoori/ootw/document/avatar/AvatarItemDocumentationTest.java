package com.backendoori.ootw.document.avatar;

import static com.backendoori.ootw.document.common.ApiDocumentUtil.field;
import static com.backendoori.ootw.document.common.ApiDocumentUtil.getDocumentRequest;
import static com.backendoori.ootw.document.common.ApiDocumentUtil.getDocumentResponse;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.List;
import com.backendoori.ootw.avatar.domain.ItemType;
import com.backendoori.ootw.avatar.domain.Sex;
import com.backendoori.ootw.avatar.dto.AvatarItemRequest;
import com.backendoori.ootw.avatar.dto.AvatarItemResponse;
import com.backendoori.ootw.avatar.service.AvatarItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WithMockUser
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class AvatarItemDocumentationTest {

    static final Faker FAKER = new Faker();
    static final String API = "/api/v1/avatar-items";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    AvatarItemService avatarItemService;

    @DisplayName("[POST] upload 201 Created")
    @Test
    public void testUploadCreated() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt",
            "image/jpeg", "some xml".getBytes());
        AvatarItemRequest requestDto = new AvatarItemRequest("HAIR", Sex.MALE.name());
        MockMultipartFile request = new MockMultipartFile("request", "filename.txt",
            "application/json", objectMapper.writeValueAsBytes(requestDto));
        AvatarItemResponse avatarItemResponse =
            new AvatarItemResponse((long) FAKER.number().positive(), requestDto.type(), requestDto.sex(),
                FAKER.internet().url());

        given(avatarItemService.upload(file, requestDto))
            .willReturn(avatarItemResponse);

        // when
        ResultActions actions = mockMvc.perform(multipart(API)
            .file(file)
            .file(request)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .accept(MediaType.APPLICATION_JSON)
            .characterEncoding(StandardCharsets.UTF_8));

        // then
        actions.andExpect(status().isCreated())
            .andDo(
                document("avatar-image-upload",
                    getDocumentRequest(),
                    getDocumentResponse(),
//                    TODO: 해당 API가 토큰이 필요 없는지 논의 필요
//                    requestHeaders(
//                        headerWithName("Authorization").description("JWT 토큰")
//                    ),
                    requestParts(
                        partWithName("file").description("아바타 이미지 파일"),
                        partWithName("request").description("아바타 이미지 상세 정보")
                    ),
                    requestPartFields(
                        "request",
                        fieldWithPath("type").description("아바타 이미지 타입"),
                        fieldWithPath("sex").description("아바타 이미지 성별")
                    ),
                    responseFields(
                        field("avatarItemId", JsonFieldType.NUMBER, "아바타 이미지 ID"),
                        field("type", JsonFieldType.STRING, "아바타 이미지 타입"),
                        field("sex", JsonFieldType.STRING, "아바타 이미지 성별"),
                        field("url", JsonFieldType.STRING, "아바타 이미지 URL")
                    )
                )
            );
    }

    @DisplayName("[POST] getAll 200 Ok")
    @Test
    public void testGetAllOk() throws Exception {
        // given
        given(avatarItemService.getList())
            .willReturn(List.of(
                new AvatarItemResponse(1L, ItemType.HAIR.name(), Sex.MALE.name(), FAKER.internet().url()),
                new AvatarItemResponse(2L, ItemType.TOP.name(), Sex.FEMALE.name(), FAKER.internet().url())
            ));

        // when
        ResultActions actions = mockMvc.perform(get(API)
            .accept(MediaType.APPLICATION_JSON));

        // then
        actions.andExpect(status().isOk())
            .andDo(
                document("avatar-image-get-all",
                    getDocumentRequest(),
                    getDocumentResponse(),
//                    TODO: 해당 API가 토큰이 필요 없는지 논의 필요
//                    requestHeaders(
//                        headerWithName("Authorization").description("JWT 토큰")
//                    ),
                    responseFields(
                        field("[].avatarItemId", JsonFieldType.NUMBER, "아바타 이미지 ID"),
                        field("[].type", JsonFieldType.STRING, "아바타 이미지 타입"),
                        field("[].sex", JsonFieldType.STRING, "아바타 이미지 성별"),
                        field("[].url", JsonFieldType.STRING, "아바타 이미지 URL")
                    )
                )
            );
    }

}
