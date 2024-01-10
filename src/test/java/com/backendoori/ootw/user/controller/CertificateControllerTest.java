package com.backendoori.ootw.user.controller;

import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backendoori.ootw.exception.UserNotFoundException;
import com.backendoori.ootw.security.jwt.TokenProvider;
import com.backendoori.ootw.user.dto.CertifyDto;
import com.backendoori.ootw.user.exception.AlreadyCertifiedUserException;
import com.backendoori.ootw.user.exception.IncorrectCertificateException;
import com.backendoori.ootw.user.service.CertificateService;
import net.datafaker.Faker;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@WithMockUser
@WebMvcTest(CertificateController.class)
class CertificateControllerTest {

    static final Faker FAKER = new Faker();

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CertificateService certificateService;
    @MockBean
    TokenProvider tokenProvider;

    CertifyDto certifyDto;

    @BeforeEach
    void setup() {
        long userId = FAKER.number().positive();
        String code = RandomStringUtils.random(CertificateService.CERTIFICATE_SIZE);

        certifyDto = new CertifyDto(userId, code);
    }

    @DisplayName("이메일 인증에 성공하면 200 status를 반환한다")
    @Test
    void testCertifyOk() throws Exception {
        // given
        MockHttpServletRequestBuilder requestBuilder = patch("/api/v1/auth/certify")
            .with(csrf())
            .param("userId", certifyDto.userId().toString())
            .param("code", certifyDto.code())
            .contentType(MediaType.APPLICATION_JSON);

        // when
        ResultActions actions = mockMvc.perform(requestBuilder);

        // then
        actions.andExpect((status().isOk()));
    }

    @DisplayName("존재하지 않는 사용자의 경우 404 status를 반환한다")
    @Test
    void testCertifyNotFound() throws Exception {
        // given
        MockHttpServletRequestBuilder requestBuilder = patch("/api/v1/auth/certify")
            .with(csrf())
            .param("userId", certifyDto.userId().toString())
            .param("code", certifyDto.code())
            .contentType(MediaType.APPLICATION_JSON);

        doThrow(UserNotFoundException.class)
            .when(certificateService)
            .certify(certifyDto);

        // when
        ResultActions actions = mockMvc.perform(requestBuilder);

        // then
        actions.andExpect((status().isNotFound()));
    }

    @DisplayName("이미 인증된 사용자의 경우 208 status를 반환한다")
    @Test
    void testCertifyAlreadyReported() throws Exception {
        // given
        MockHttpServletRequestBuilder requestBuilder = patch("/api/v1/auth/certify")
            .with(csrf())
            .param("userId", certifyDto.userId().toString())
            .param("code", certifyDto.code())
            .contentType(MediaType.APPLICATION_JSON);

        doThrow(AlreadyCertifiedUserException.class)
            .when(certificateService)
            .certify(certifyDto);

        // when
        ResultActions actions = mockMvc.perform(requestBuilder);

        // then
        actions.andExpect((status().isAlreadyReported()));
    }

    @DisplayName("인증 코드가 다른 경우 401 status를 반환한다")
    @Test
    void testCertifyUnauthorized() throws Exception {
        // given
        MockHttpServletRequestBuilder requestBuilder = patch("/api/v1/auth/certify")
            .with(csrf())
            .param("userId", certifyDto.userId().toString())
            .param("code", certifyDto.code())
            .contentType(MediaType.APPLICATION_JSON);

        doThrow(IncorrectCertificateException.class)
            .when(certificateService)
            .certify(certifyDto);

        // when
        ResultActions actions = mockMvc.perform(requestBuilder);

        // then
        actions.andExpect((status().isUnauthorized()));
    }

}
