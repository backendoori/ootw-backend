package com.backendoori.ootw.user.domain;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.util.stream.Stream;
import com.backendoori.ootw.user.validation.Message;
import net.datafaker.Faker;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class UserTest {

    static final Faker faker = new Faker();

    Long id;
    String email;
    String password;
    String nickname;
    String image;

    @BeforeEach
    void setup() {
        id = (long) faker.number().positive();
        email = faker.internet().emailAddress();
        password = faker.internet().password();
        nickname = faker.internet().username();
        image = faker.internet().url();
    }

    @DisplayName("instance 생성에 성공한다.")
    @Test
    void testCreate() {
        // given

        // when
        ThrowingCallable createUser = this::buildUser;

        // then
        assertThatNoException().isThrownBy(createUser);
    }

    @DisplayName("잘못된 형식의 이메일인 경우 생성에 실패한다.")
    @NullAndEmptySource
    @MethodSource("generateInvalidEmails")
    @ParameterizedTest()
    void testCreateInvalidEmail(String email) {
        // given
        this.email = email;

        // when
        ThrowingCallable createUser = this::buildUser;

        // then
        assertThatIllegalArgumentException()
            .isThrownBy(createUser)
            .withMessage(Message.INVALID_EMAIL);
    }

    @DisplayName("비밀번호가 공백인 경우 생성에 실패한다.")
    @NullAndEmptySource
    @ParameterizedTest
    void testCreateBlankPassword(String password) {
        // given
        this.password = password;

        // when
        ThrowingCallable createUser = this::buildUser;

        // then
        assertThatIllegalArgumentException()
            .isThrownBy(createUser)
            .withMessage(Message.BLANK_PASSWORD);
    }

    @DisplayName("닉네임이 공백인 경우 생성에 실패한다.")
    @NullAndEmptySource
    @ParameterizedTest
    void testCreateBlankNickName(String nickname) {
        // given
        this.nickname = nickname;

        // when
        ThrowingCallable createUser = this::buildUser;

        // then
        assertThatIllegalArgumentException()
            .isThrownBy(createUser)
            .withMessage(Message.BLANK_NICKNAME);
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

    private User buildUser() {
        return User.builder()
            .id(id)
            .email(email)
            .password(password)
            .nickname(nickname)
            .image(image)
            .build();
    }

}
