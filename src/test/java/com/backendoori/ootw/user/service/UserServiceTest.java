package com.backendoori.ootw.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.util.stream.Stream;
import com.backendoori.ootw.exception.UserNotFoundException;
import com.backendoori.ootw.user.domain.User;
import com.backendoori.ootw.user.dto.LoginDto;
import com.backendoori.ootw.user.dto.SignupDto;
import com.backendoori.ootw.user.dto.TokenDto;
import com.backendoori.ootw.user.exception.AlreadyExistEmailException;
import com.backendoori.ootw.user.exception.IncorrectPasswordException;
import com.backendoori.ootw.user.exception.NonCertifiedUserException;
import com.backendoori.ootw.user.repository.UserRepository;
import com.backendoori.ootw.user.validation.Message;
import net.datafaker.Faker;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
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
            assertThatExceptionOfType(AlreadyExistEmailException.class)
                .isThrownBy(signup)
                .withMessage(AlreadyExistEmailException.DEFAULT_MESSAGE);
        }

        @DisplayName("비밀번호 형식이 올바르지 않을 경우 회원가입에 실패한다")
        @NullAndEmptySource
        @MethodSource("generateInvalidPasswords")
        @ParameterizedTest
        void failInvalidPassword(String password) {
            // given
            SignupDto signupDto = generateSignupDto(password);

            // when
            ThrowingCallable signup = () -> userService.signup(signupDto);

            // then
            assertThatIllegalArgumentException()
                .isThrownBy(signup)
                .withMessage(Message.INVALID_PASSWORD);
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

        @DisplayName("로그인에 성공하면 토큰을 반환한다")
        @Test
        void success() {
            // given
            String password = faker.internet().password();
            User user = userRepository.save(generateUser(password, true));
            LoginDto loginDto = new LoginDto(user.getEmail(), password);

            // when
            TokenDto tokenDto = userService.login(loginDto);

            // then
            assertThat(tokenDto.token())
                .isInstanceOf(String.class)
                .isNotNull()
                .isNotBlank();
        }

        @DisplayName("email이 일치하는 사용자가 없으면 로그인에 실패한다")
        @Test
        void failUserNotFound() {
            // given
            String password = faker.internet().password();
            User user = generateUser(password);
            LoginDto loginDto = new LoginDto(user.getEmail(), password + password);

            // when
            ThrowingCallable login = () -> userService.login(loginDto);

            // then
            assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(login)
                .withMessage(UserNotFoundException.DEFAULT_MESSAGE);
        }

        @DisplayName("비밀번호가 일치하지 않으면 로그인에 실패한다")
        @Test
        void failIncorrectPassword() {
            // given
            String password = faker.internet().password();
            User user = userRepository.save(generateUser(password, true));
            LoginDto loginDto = new LoginDto(user.getEmail(), password + password);

            // when
            ThrowingCallable login = () -> userService.login(loginDto);

            // then
            assertThatExceptionOfType(IncorrectPasswordException.class)
                .isThrownBy(login)
                .withMessage(IncorrectPasswordException.DEFAULT_MESSAGE);
        }

        @DisplayName("이메일이 인증되지 않으면 로그인에 실패한다")
        @Test
        void failNonCertified() {
            // given
            String password = faker.internet().password();
            User user = userRepository.save(generateUser(password, false));
            LoginDto loginDto = new LoginDto(user.getEmail(), password + password);

            // when
            ThrowingCallable login = () -> userService.login(loginDto);

            // then
            assertThatExceptionOfType(NonCertifiedUserException.class)
                .isThrownBy(login)
                .withMessage(NonCertifiedUserException.DEFAULT_MESSAGE);
        }

    }

    private SignupDto generateSignupDto() {
        return generateSignupDto(faker.internet().password(8, 30, true, true, true));
    }

    private SignupDto generateSignupDto(String password) {
        String email = faker.internet().emailAddress();
        String nickname = faker.internet().username();

        return new SignupDto(email, password, nickname);
    }

    private User generateUser(String password) {
        return generateUser(password, false);
    }

    private User generateUser(String password, boolean certified) {
        return User.builder()
            .email(faker.internet().emailAddress())
            .password(passwordEncoder.encode(password))
            .nickname(faker.internet().username())
            .image(faker.internet().url())
            .certified(certified)
            .build();
    }

}
