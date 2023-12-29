package com.backendoori.ootw.user.controller;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import com.backendoori.ootw.config.jwt.TokenProvider;
import com.backendoori.ootw.exception.AlreadyExistEmailException;
import com.backendoori.ootw.exception.IncorrectPasswordException;
import com.backendoori.ootw.exception.NotExistUserException;
import com.backendoori.ootw.user.dto.LoginDto;
import com.backendoori.ootw.user.dto.SignupDto;
import com.backendoori.ootw.user.dto.TokenDto;
import com.backendoori.ootw.user.dto.UserDto;
import com.backendoori.ootw.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WithMockUser
@WebMvcTest(UserController.class)
class UserControllerTest {

    static Faker faker = new Faker();

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserService userService;
    @MockBean
    TokenProvider tokenProvider;

    @DisplayName("회원가입 테스트")
    @Nested
    class SignupTest {

        @DisplayName("회원가입에 성공하면 201 status와 생성한 유저 정보를 반환한다")
        @Test
        void created() throws Exception {
            // given
            SignupDto signupDto = generateSignupDto();
            UserDto userDto = createUser(signupDto);

            given(userService.signup(signupDto)).willReturn(userDto);

            // when
            ResultActions actions = mockMvc.perform(
                post("/api/v1/auth/signup")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signupDto)));

            // then
            actions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(userDto.id()), Long.class))
                .andExpect(jsonPath("$.email", is(userDto.email())))
                .andExpect(jsonPath("$.nickname", is(userDto.nickname())))
                .andExpect(jsonPath("$.image", is(userDto.image())))
                .andExpect(jsonPath("$.createdAt", startsWith(removeMills(userDto.createdAt()))))
                .andExpect(jsonPath("$.updatedAt", startsWith(removeMills(userDto.updatedAt()))));
        }

        // TODO: 추후 409 status를 반환하도록 rest controller advice 추가
        @DisplayName("이미 등록된 email일 경우 401 status를 반환한다")
        @Test
        void unauthorizedAlreadyExistEmail() throws Exception {
            // given
            SignupDto signupDto = generateSignupDto();

            given(userService.signup(signupDto)).willThrow(new AlreadyExistEmailException());

            // when
            ResultActions actions = mockMvc.perform(
                post("/api/v1/auth/signup")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signupDto)));

            // then
            actions.andExpect(status().isUnauthorized());
        }

    }

    @DisplayName("로그인 테스트")
    @Nested
    class LoginTest {

        @DisplayName("로그인에 성공하면 201 status와 토큰을 반환한다")
        @Test
        void created() throws Exception {
            // given
            LoginDto loginDto = generateLoginDto();
            TokenDto tokenDto = new TokenDto(faker.hashing().sha512());

            given(userService.login(loginDto)).willReturn(tokenDto);

            // when
            ResultActions actions = mockMvc.perform(
                post("/api/v1/auth/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginDto)));

            // then
            actions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", is(tokenDto.token())));
        }

        @DisplayName("email이 일치하는 사용자가 없으면 401 status를 반환한다")
        @Test
        void unauthorizedNotExistUser() throws Exception {
            // given
            LoginDto loginDto = generateLoginDto();

            given(userService.login(loginDto)).willThrow(new NotExistUserException());

            // when
            ResultActions actions = mockMvc.perform(
                post("/api/v1/auth/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginDto)));

            // then
            actions.andExpect(status().isUnauthorized());
        }

        @DisplayName("비밀번호가 일치하지 않으면 401 status를 반환한다")
        @Test
        void unauthorizedIncorrectPassword() throws Exception {
            // given
            LoginDto loginDto = generateLoginDto();

            given(userService.login(loginDto)).willThrow(new IncorrectPasswordException());

            // when
            ResultActions actions = mockMvc.perform(
                post("/api/v1/auth/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginDto)));

            // then
            actions.andExpect(status().isUnauthorized());
        }

    }

    private SignupDto generateSignupDto() {
        String email = faker.internet().emailAddress();
        String password = faker.internet().password();
        String nickname = faker.internet().username();
        String image = faker.internet().url();

        return new SignupDto(email, password, nickname, image);
    }

    private UserDto createUser(SignupDto signupDto) {
        return UserDto.builder()
            .id((long) faker.number().positive())
            .email(signupDto.email())
            .nickname(signupDto.nickname())
            .image(signupDto.email())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    }

    private LoginDto generateLoginDto() {
        String email = faker.internet().emailAddress();
        String password = faker.internet().password();

        return new LoginDto(email, password);
    }

    private String removeMills(LocalDateTime localDateTime) {
        return localDateTime.truncatedTo(ChronoUnit.SECONDS).toString();
    }

}
