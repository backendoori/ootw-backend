package com.backendoori.ootw.avatar.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import com.backendoori.ootw.avatar.domain.Sex;
import com.backendoori.ootw.avatar.dto.AvatarItemRequest;
import com.backendoori.ootw.avatar.repository.AvatarItemRepository;
import com.backendoori.ootw.avatar.service.AvatarItemService;
import com.backendoori.ootw.common.image.exception.ImageException;
import com.backendoori.ootw.common.image.exception.SaveException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

@WithMockUser
@AutoConfigureMockMvc
@SpringBootTest
class AvatarItemControllerTest {

    @MockBean
    AvatarItemService avatarItemService;

    @MockBean
    AvatarItemRepository avatarItemRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("아바타 이미지를 정상적으로 등록한다.")
    public void imageUploadTest() throws Exception {
        //given
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt",
            "image/jpeg", "some xml".getBytes());
        AvatarItemRequest requestDto = new AvatarItemRequest("HAIR", Sex.MALE.name());
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

    @Test
    @DisplayName("아바타 등록 요청 중 이미지 등록 중 예외가 발생하면 커스텀 예외가 발생한다.")
    public void imageUploadException() throws Exception {
        //given
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "image/jpeg", "some xml".getBytes());
        AvatarItemRequest requestDto = new AvatarItemRequest("HAIR", Sex.MALE.name());
        MockMultipartFile request = new MockMultipartFile("request", "filename.txt", "application/json",
            objectMapper.writeValueAsBytes(requestDto));

        doThrow(new ImageException("Mock Exception"))
            .when(avatarItemService)
            .uploadItem(any(MultipartFile.class), any(AvatarItemRequest.class));

        //when, then
        mockMvc.perform(multipart("/api/v1/avatar-items")
                .file(file)
                .file(request)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("아바타 등록 요청 중 롤백 일어날 시 이미 사진이 저장된 상황이면 예외를 발생시키고 사진을 지운다.")
    public void imageSaveException() throws Exception {
        //given
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "image/jpeg", "some xml".getBytes());
        AvatarItemRequest requestDto = new AvatarItemRequest("HAIR", Sex.MALE.name());
        MockMultipartFile request = new MockMultipartFile("request", "filename.txt", "application/json",
            objectMapper.writeValueAsBytes(requestDto));

        doThrow(new SaveException())
            .when(avatarItemService)
            .uploadItem(any(MultipartFile.class), any(AvatarItemRequest.class));

        //when, then
        mockMvc.perform(multipart("/api/v1/avatar-items")
                .file(file)
                .file(request)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8))
            .andExpect(status().isUnprocessableEntity());
    }

    @ParameterizedTest(name = "[{index}] content-type 이 {0}인 경우")
    @ValueSource(strings = {"text/plain", "application/json"})
    @DisplayName("아바타 이미지 업로드 시 파일의 유형이 이미지가 아닌 경우 예외가 발생한다.")
    public void imageUpLoadWithInvalidContentType(String contentType) throws Exception {
        //given
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt",
            contentType, "some xml".getBytes());
        AvatarItemRequest dto = new AvatarItemRequest("HAIR", Sex.MALE.name());
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
    @DisplayName("아바타 이미지 업로드 시 이미지 파일이 없는 경우 예외가 발생한다.")
    public void noImageUpLoad() throws Exception {
        //given
        MockMultipartFile file = new MockMultipartFile("file", "", "image/png", new byte[0]);
        AvatarItemRequest dto = new AvatarItemRequest("HAIR", Sex.MALE.name());
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

    @ParameterizedTest(name = "[{index}] item type으로  {0}가 들어오는 경우")
    @ValueSource(strings = {"afsee", "hair"})
    @NullAndEmptySource
    @DisplayName("아바타 이미지 업로드 시 아이템 타입이 존재하지 않는 경우 예외가 발생한다.")
    public void upLoadWithInvalidRequest(String type) throws Exception {
        //given
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt",
            "image/jpeg", "some xml".getBytes());
        AvatarItemRequest requestDto = new AvatarItemRequest(type, Sex.MALE.name());
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

    @Test
    @DisplayName("아바타 이미지 리스트 조회에 성공한다.")
    public void getItemList() throws Exception {
        //given, when, then
        mockMvc.perform(get("/api/v1/avatar-items"))
            .andExpect(status().isOk())
            .andDo(print());
    }
}
