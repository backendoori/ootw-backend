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
import java.util.stream.Stream;
import com.backendoori.ootw.exception.UserNotFoundException;
import com.backendoori.ootw.security.jwt.TokenProvider;
import com.backendoori.ootw.user.dto.LoginDto;
import com.backendoori.ootw.user.dto.SignupDto;
import com.backendoori.ootw.user.dto.TokenDto;
import com.backendoori.ootw.user.dto.UserDto;
import com.backendoori.ootw.user.exception.AlreadyExistEmailException;
import com.backendoori.ootw.user.exception.IncorrectPasswordException;
import com.backendoori.ootw.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
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

        @DisplayName("잘못된 형식의 email일 경우 400 status를 반환한다")
        @NullAndEmptySource
        @MethodSource("generateInvalidEmails")
        @ParameterizedTest
        void badRequestInvalidEmail(String email) throws Exception {
            // given
            String password = faker.internet().password(8, 30, true, true, true);
            String nickname = faker.internet().username();
            SignupDto signupDto = new SignupDto(email, password, nickname);

            // when
            ResultActions actions = mockMvc.perform(
                post("/api/v1/auth/signup")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signupDto)));

            // then
            actions.andExpect(status().isBadRequest());
        }

        @DisplayName("잘못된 형식의 비밀번호의 경우 400 status를 반환한다")
        @NullAndEmptySource
        @MethodSource("generateInvalidPasswords")
        @ParameterizedTest
        void badRequestInvalidPassword(String password) throws Exception {
            // given
            String email = faker.internet().emailAddress();
            String nickname = faker.internet().username();
            SignupDto signupDto = new SignupDto(email, password, nickname);

            // when
            ResultActions actions = mockMvc.perform(
                post("/api/v1/auth/signup")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signupDto)));

            // then
            actions.andExpect(status().isBadRequest());
        }

        @DisplayName("닉네임이 공백일 경우 400 status를 반환한다")
        @NullAndEmptySource
        @ParameterizedTest
        void badRequestBlankNickname(String nickname) throws Exception {
            // given
            String email = faker.internet().emailAddress();
            String password = faker.internet().password(8, 30, true, true, true);
            SignupDto signupDto = new SignupDto(email, password, nickname);

            // when
            ResultActions actions = mockMvc.perform(
                post("/api/v1/auth/signup")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signupDto)));

            // then
            actions.andExpect(status().isBadRequest());
        }

        @DisplayName("이미 등록된 email일 경우 409 status를 반환한다")
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
            actions.andExpect(status().isConflict());
        }

        private static Stream<String> generateInvalidEmails() {
            return Stream.of(
                faker.app().name(),
                faker.name().fullName(),
                faker.internet().url(),
                faker.internet().domainName(),
                faker.internet().webdomain(),
                faker.internet().botUserAgentAny()
            );
        }

        private static Stream<String> generateInvalidPasswords() {
            return Stream.of(
                faker.internet().password(1, 7, true, true, true),
                faker.internet().password(31, 50, true, true, true),
                faker.internet().password(8, 30, true, false, true),
                faker.internet().password(8, 30, true, true, false)
            );
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

        @DisplayName("email이 일치하는 사용자가 없으면 404 status를 반환한다")
        @Test
        void unauthorizedNotExistUser() throws Exception {
            // given
            LoginDto loginDto = generateLoginDto();

            given(userService.login(loginDto)).willThrow(new UserNotFoundException());

            // when
            ResultActions actions = mockMvc.perform(
                post("/api/v1/auth/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginDto)));

            // then
            actions.andExpect(status().isNotFound());
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
        String password = faker.internet().password(8, 30, true, true, true);
        String nickname = faker.internet().username();

        return new SignupDto(email, password, nickname);
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
