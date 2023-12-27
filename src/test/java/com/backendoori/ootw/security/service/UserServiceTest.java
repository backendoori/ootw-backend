package com.backendoori.ootw.security.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

import com.backendoori.ootw.domain.User;
import com.backendoori.ootw.security.dto.LoginDto;
import com.backendoori.ootw.security.dto.SignupDto;
import com.backendoori.ootw.security.dto.TokenDto;
import com.backendoori.ootw.security.exception.AlreadyExistEmailException;
import com.backendoori.ootw.security.exception.IncorrectPasswordException;
import com.backendoori.ootw.security.exception.NotExistUserException;
import com.backendoori.ootw.security.repository.UserRepository;
import net.datafaker.Faker;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest()
@TestInstance(Lifecycle.PER_CLASS)
class UserServiceTest {

    static Faker faker = new Faker();

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserService userService;

    @BeforeAll
    @AfterEach
    void cleanup() {
        userRepository.deleteAll();
    }

    @DisplayName("회원가입 테스트")
    @Nested
    class SignupTest {

        @DisplayName("회원가입에 성공한다")
        @Test
        void success() {
            // given
            SignupDto signupDto = generateSignupDto();

            // when
            ThrowingCallable signup = () -> userService.signup(signupDto);

            // then
            assertThatNoException().isThrownBy(signup);
        }

        @DisplayName("이미 사용 중인 email일 경우 회원가입에 실패한다")
        @Test
        void failAlreadyExistUser() {
            // given
            SignupDto signupDto = generateSignupDto();

            userService.signup(signupDto);

            // when
            ThrowingCallable signup = () -> userService.signup(signupDto);

            // then
            assertThatExceptionOfType(AlreadyExistEmailException.class).isThrownBy(signup)
                .withMessage(AlreadyExistEmailException.DEFAULT_MESSAGE);
        }

    }

    @DisplayName("로그인 테스트")
    @Nested
    class LoginTest {

        @DisplayName("로그인에 성공하면 토큰을 반환한다")
        @Test
        void success() {
            // given
            String password = faker.internet().password();
            User user = userRepository.save(generateUser(password));
            LoginDto loginDto = new LoginDto(user.getEmail(), password);

            // when
            TokenDto tokenDto = userService.login(loginDto);

            // then
            assertThat(tokenDto.token()).isInstanceOf(String.class)
                .isNotNull()
                .isNotBlank();
        }

        @DisplayName("email이 일치하는 사용자가 없으면 로그인에 실패한다")
        @Test
        void failNotExistUser() {
            // given
            String password = faker.internet().password();
            User user = generateUser(password);
            LoginDto loginDto = new LoginDto(user.getEmail(), password + password);

            // when
            ThrowingCallable login = () -> userService.login(loginDto);

            // then
            assertThatExceptionOfType(NotExistUserException.class).isThrownBy(login)
                .withMessage(NotExistUserException.DEFAULT_MESSAGE);
        }

        @DisplayName("비밀번호가 일치하지 않으면 로그인에 실패한다")
        @Test
        void failIncorrectPassword() {
            // given
            String password = faker.internet().password();
            User user = userRepository.save(generateUser(password));
            LoginDto loginDto = new LoginDto(user.getEmail(), password + password);

            // when
            ThrowingCallable login = () -> userService.login(loginDto);

            // then
            assertThatExceptionOfType(IncorrectPasswordException.class).isThrownBy(login)
                .withMessage(IncorrectPasswordException.DEFAULT_MESSAGE);
        }

    }

    private SignupDto generateSignupDto() {
        String email = faker.internet().emailAddress();
        String password = faker.internet().password();
        String nickname = faker.internet().username();
        String image = faker.internet().url();

        return new SignupDto(email, password, nickname, image);
    }

    private User generateUser(String password) {
        return User.builder()
            .email(faker.internet().emailAddress())
            .password(passwordEncoder.encode(password))
            .nickname(faker.internet().username())
            .image(faker.internet().url())
            .build();
    }

}
