package com.backendoori.ootw.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backendoori.ootw.domain.Type;
import com.backendoori.ootw.dto.AvatarAppearanceRequest;
import com.backendoori.ootw.service.AvatarAppearanceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@SpringBootTest
class AvatarAppearanceControllerTest {

    @MockBean
    AvatarAppearanceService postService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("아바타 이미지 업로드 api 테스트")
    public void imageUploadTest() throws Exception {
        //given
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt",
            "text/plain", "some xml".getBytes());
        AvatarAppearanceRequest request = new AvatarAppearanceRequest("HAIR", true);
        String requestJson = objectMapper.writeValueAsString(request);

        //when, then
        mockMvc.perform(multipart("/api/v1/image")
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .content(requestJson)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
            .andExpect(status().isCreated());
    }

}
