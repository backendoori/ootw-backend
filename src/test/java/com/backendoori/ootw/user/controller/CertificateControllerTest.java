package com.backendoori.ootw.user.controller;

import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;
import com.backendoori.ootw.exception.UserNotFoundException;
import com.backendoori.ootw.security.jwt.TokenProvider;
import com.backendoori.ootw.user.domain.Certificate;
import com.backendoori.ootw.user.dto.CertifyDto;
import com.backendoori.ootw.user.dto.SendCodeDto;
import com.backendoori.ootw.user.exception.AlreadyCertifiedUserException;
import com.backendoori.ootw.user.exception.IncorrectCertificateException;
import com.backendoori.ootw.user.service.CertificateService;
import net.datafaker.Faker;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
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

    @DisplayName("인증 코드 발송 테스트")
    @Nested
    class CertificationTest {

        SendCodeDto sendCodeDto;

        @BeforeEach
        void setSendCertificateDto() {
            sendCodeDto = new SendCodeDto(FAKER.internet().emailAddress());
        }

        @DisplayName("인증 코드 발송에 성공할 경우 200 status를 반환한다.")
        @Test
        void ok() throws Exception {
            // given
            MockHttpServletRequestBuilder requestBuilder = patch("/api/v1/auth/certificate")
                .with(csrf())
                .param("email", sendCodeDto.email())
                .contentType(MediaType.APPLICATION_JSON);

            // when
            ResultActions actions = mockMvc.perform(requestBuilder);

            // then
            actions.andExpect((status().isOk()));
        }

        @DisplayName("이미 인증된 사용자의 경우 208 status를 반환한다.")
        @Test
        void alreadyReported() throws Exception {
            // given
            MockHttpServletRequestBuilder requestBuilder = patch("/api/v1/auth/certificate")
                .with(csrf())
                .param("email", sendCodeDto.email())
                .contentType(MediaType.APPLICATION_JSON);

            doThrow(AlreadyCertifiedUserException.class)
                .when(certificateService)
                .sendCertificate(sendCodeDto);

            // when
            ResultActions actions = mockMvc.perform(requestBuilder);

            // then
            actions.andExpect((status().isAlreadyReported()));
        }

        @DisplayName("이메일 형식이 올바르지 않은 경우 400 status를 반환한다.")
        @NullAndEmptySource
        @ArgumentsSource(InvalidEmailProvider.class)
        @ParameterizedTest
        void badRequest(String email) throws Exception {
            // given
            MockHttpServletRequestBuilder requestBuilder = patch("/api/v1/auth/certificate")
                .with(csrf())
                .param("email", email)
                .contentType(MediaType.APPLICATION_JSON);

            // when
            ResultActions actions = mockMvc.perform(requestBuilder);

            // then
            actions.andExpect((status().isBadRequest()));
        }

        static class InvalidEmailProvider implements ArgumentsProvider {

            @Override
            public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
                return Stream.of(
                    Arguments.of(FAKER.app().name()),
                    Arguments.of(FAKER.name().fullName()),
                    Arguments.of(FAKER.internet().url()),
                    Arguments.of(FAKER.internet().domainName()),
                    Arguments.of(FAKER.internet().webdomain()),
                    Arguments.of(FAKER.internet().botUserAgentAny())
                );
            }

        }

    }

    @DisplayName("사용자 코드 인증 테스트")
    @Nested
    class CertifyTest {

        CertifyDto certifyDto;

        @BeforeEach
        void setup() {
            String email = FAKER.internet().safeEmailAddress();
            String code = RandomStringUtils.randomAlphanumeric(Certificate.SIZE);

            certifyDto = new CertifyDto(email, code);
        }

        @DisplayName("이메일 인증에 성공하면 200 status를 반환한다")
        @Test
        void ok() throws Exception {
            // given
            MockHttpServletRequestBuilder requestBuilder = patch("/api/v1/auth/certify")
                .with(csrf())
                .param("email", certifyDto.email())
                .param("code", certifyDto.code())
                .contentType(MediaType.APPLICATION_JSON);

            // when
            ResultActions actions = mockMvc.perform(requestBuilder);

            // then
            actions.andExpect((status().isOk()));
        }

        @DisplayName("존재하지 않는 사용자의 경우 404 status를 반환한다")
        @Test
        void notFound() throws Exception {
            // given
            MockHttpServletRequestBuilder requestBuilder = patch("/api/v1/auth/certify")
                .with(csrf())
                .param("email", certifyDto.email())
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
        void alreadyReported() throws Exception {
            // given
            MockHttpServletRequestBuilder requestBuilder = patch("/api/v1/auth/certify")
                .with(csrf())
                .param("email", certifyDto.email())
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
        void unauthorized() throws Exception {
            // given
            MockHttpServletRequestBuilder requestBuilder = patch("/api/v1/auth/certify")
                .with(csrf())
                .param("email", certifyDto.email())
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

}
