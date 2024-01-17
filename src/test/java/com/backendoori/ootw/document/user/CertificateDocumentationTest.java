package com.backendoori.ootw.document.user;

import static com.backendoori.ootw.document.common.ApiDocumentUtil.getDocumentRequest;
import static com.backendoori.ootw.document.common.ApiDocumentUtil.getDocumentResponse;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backendoori.ootw.user.domain.Certificate;
import com.backendoori.ootw.user.dto.CertifyDto;
import com.backendoori.ootw.user.dto.SendCodeDto;
import com.backendoori.ootw.user.service.CertificateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.datafaker.Faker;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@WithMockUser
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class CertificateDocumentationTest {

    static final String API_PREFIX = "/api/v1/auth";
    static final Faker FAKER = new Faker();

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CertificateService certificateService;

    @DisplayName("[PATCH] certificate 200 Ok")
    @Test
    void testCertificateOk() throws Exception {
        // given
        SendCodeDto sendCodeDto = new SendCodeDto(FAKER.internet().emailAddress());
        MockHttpServletRequestBuilder requestBuilder = patch(API_PREFIX + "/certificate")
            .queryParam("email", sendCodeDto.email())
            .contentType(MediaType.APPLICATION_JSON);

        // when
        ResultActions actions = mockMvc.perform(requestBuilder);

        // then
        actions.andExpect((status().isOk()))
            .andDo(
                document("certificate",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    queryParameters(
                        parameterWithName("email").description("Email 주소")
                    )
                )
            );
    }

    @DisplayName("[PATCH] certify 200 Ok")
    @Test
    void testCertifyOk() throws Exception {
        // given
        String email = FAKER.internet().safeEmailAddress();
        String code = RandomStringUtils.randomAlphanumeric(Certificate.SIZE);
        CertifyDto certifyDto = new CertifyDto(email, code);
        MockHttpServletRequestBuilder requestBuilder = patch(API_PREFIX + "/certify")
            .queryParam("email", certifyDto.email())
            .queryParam("code", certifyDto.code())
            .contentType(MediaType.APPLICATION_JSON);

        // when
        ResultActions actions = mockMvc.perform(requestBuilder);

        // then
        actions.andExpect((status().isOk()))
            .andDo(
                document("certify",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    queryParameters(
                        parameterWithName("email").description("Email 주소"),
                        parameterWithName("code").description("Email 인증 코드")
                    )
                )
            );
    }

}
