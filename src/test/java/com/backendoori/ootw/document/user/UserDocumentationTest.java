package com.backendoori.ootw.document.user;

import static com.backendoori.ootw.document.common.ApiDocumentUtil.field;
import static com.backendoori.ootw.document.common.ApiDocumentUtil.getDocumentRequest;
import static com.backendoori.ootw.document.common.ApiDocumentUtil.getDocumentResponse;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backendoori.ootw.user.dto.LoginDto;
import com.backendoori.ootw.user.dto.SignupDto;
import com.backendoori.ootw.user.dto.TokenDto;
import com.backendoori.ootw.user.service.UserService;
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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WithMockUser
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class UserDocumentationTest {

    static final String API_PREFIX = "/api/v1/auth";
    static final Faker FAKER = new Faker();

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    @DisplayName("[POST] signup 201 Created")
    @Test
    void testSignupCreated() throws Exception {
        // given
        SignupDto signupDto = generateSignupDto();

        willDoNothing().given(userService)
            .signup(signupDto);

        // when
        ResultActions actions = mockMvc.perform(
            post(API_PREFIX + "/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupDto)));

        // then
        actions.andExpect(status().isCreated())
            .andDo(
                document("user-signup",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    requestFields(
                        field("email", JsonFieldType.STRING, "Email 주소"),
                        field("password", JsonFieldType.STRING, "비밀번호"),
                        field("nickname", JsonFieldType.STRING, "별명")
                    )
                )
            );
    }

    @DisplayName("[POST] login 201 Created")
    @Test
    void testLoginCreated() throws Exception {
        // given
        LoginDto loginDto = generateLoginDto();
        TokenDto tokenDto = new TokenDto(FAKER.hashing().sha512());

        given(userService.login(loginDto)).willReturn(tokenDto);

        // when
        ResultActions actions = mockMvc.perform(
            post(API_PREFIX + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)));

        // then
        actions.andExpect(status().isCreated())
            .andDo(
                document("user-login",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    requestFields(
                        field("email", JsonFieldType.STRING, "Email 주소"),
                        field("password", JsonFieldType.STRING, "비밀번호")
                    ),
                    responseFields(
                        field("token", JsonFieldType.STRING, "JWT 토큰")
                    )
                )
            );
    }

    private SignupDto generateSignupDto() {
        String email = FAKER.internet().emailAddress();
        String password = FAKER.internet().password(8, 30, true, true, true);
        String nickname = FAKER.internet().username();

        return new SignupDto(email, password, nickname);
    }

    private LoginDto generateLoginDto() {
        String email = FAKER.internet().emailAddress();
        String password = FAKER.internet().password(8, 30, true, true, true);

        return new LoginDto(email, password);
    }

}
