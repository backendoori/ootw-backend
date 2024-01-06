package com.backendoori.ootw.avatar.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import com.backendoori.ootw.avatar.dto.AvatarItemRequest;
import com.backendoori.ootw.avatar.service.AvatarItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@SpringBootTest
class AvatarItemControllerTest {

    @MockBean
    AvatarItemService postService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @WithMockUser
    @DisplayName("아바타 이미지를 정상적으로 등록한다.")
    public void imageUploadTest() throws Exception {
        //given
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt",
            "image/jpeg", "some xml".getBytes());
        AvatarItemRequest requestDto = new AvatarItemRequest("HAIR", true);
        MockMultipartFile request = new MockMultipartFile("request", "filename.txt",
            "application/json", objectMapper.writeValueAsBytes(requestDto));

        //when, then
        mockMvc.perform(multipart("/api/v1/avatar-items")
                .file(file)
                .file(request)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8))
            .andExpect(status().isCreated());
    }

    @WithMockUser
    @ParameterizedTest(name = "[{index}] content-type 이 {0}인 경우")
    @ValueSource(strings = {"text/plain", "application/json"})
    @DisplayName("아바타 이미지 업로드 시 파일의 유형이 이미지가 아닌 경우 에러가 난다.")
    public void imageUpLoadWithInvalidContentType(String contentType) throws Exception {
        //given
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt",
            contentType, "some xml".getBytes());
        AvatarItemRequest dto = new AvatarItemRequest("HAIR", true);
        MockMultipartFile requestDto = new MockMultipartFile("request", "filename.json",
            MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(dto));

        //when, then
        mockMvc.perform(multipart("/api/v1/avatar-items")
                .file(file)
                .file(requestDto)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("아바타 이미지 업로드 시 이미지 파일이 없는 경우 에러가 발생한다.")
    public void noImageUpLoad() throws Exception {
        //given
        MockMultipartFile file = new MockMultipartFile("file", "", "image/png", new byte[0]);
        AvatarItemRequest dto = new AvatarItemRequest("HAIR", true);
        MockMultipartFile requestDto = new MockMultipartFile("request", "filename.json",
            MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(dto));


        //when, then
        mockMvc.perform(multipart("/api/v1/avatar-items")
                .file(file)
                .file(requestDto)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8))
            .andExpect(status().isBadRequest());
    }

    @WithMockUser
    @ParameterizedTest(name = "[{index}] item type으로  {0}가 들어오는 경우")
    @ValueSource(strings = {"afsee", "", "    ", "hair"})
    @NullSource
    @DisplayName("아바타 이미지 업로드 시 아이템 타입이 존재하지 않는 경우 에러가 난다.")
    public void UpLoadWithInvalidRequest(String type) throws Exception {
        //given
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt",
            "image/jpeg", "some xml".getBytes());
        AvatarItemRequest requestDto = new AvatarItemRequest(type, true);
        MockMultipartFile request = new MockMultipartFile("request", "filename.txt",
            "application/json", objectMapper.writeValueAsBytes(requestDto));


        //when, then
        mockMvc.perform(multipart("/api/v1/avatar-items")
                .file(file)
                .file(request)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8))
            .andExpect(status().isBadRequest());
    }
}
