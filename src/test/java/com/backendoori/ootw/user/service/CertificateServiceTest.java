package com.backendoori.ootw.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.backendoori.ootw.common.MailTest;
import com.backendoori.ootw.exception.UserNotFoundException;
import com.backendoori.ootw.user.domain.Certificate;
import com.backendoori.ootw.user.domain.User;
import com.backendoori.ootw.user.dto.CertifyDto;
import com.backendoori.ootw.user.dto.SendCodeDto;
import com.backendoori.ootw.user.exception.AlreadyCertifiedUserException;
import com.backendoori.ootw.user.exception.IncorrectCertificateException;
import com.backendoori.ootw.user.repository.CertificateRedisRepository;
import com.backendoori.ootw.user.repository.UserRepository;
import com.icegreen.greenmail.util.GreenMailUtil;
import net.datafaker.Faker;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CertificateServiceTest extends MailTest {

    static final Faker FAKER = new Faker();

    @Autowired
    CertificateService certificateService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CertificateRedisRepository certificateRedisRepository;

    User user;

    @BeforeEach
    void setup() {
        user = User.builder()
            .id((long) FAKER.number().positive())
            .email(SMTP_ADDRESS)
            .password(FAKER.internet().password())
            .nickname(FAKER.internet().username())
            .image(FAKER.internet().url())
            .certified(false)
            .build();

        userRepository.save(user);
    }

    @AfterEach
    void cleanup() {
        certificateRedisRepository.deleteById(user.getEmail());
        userRepository.deleteById(user.getId());
    }

    @DisplayName("인증 코드 발송 테스트")
    @Nested
    class SendCertificateTest {

        SendCodeDto sendCodeDto;

        @BeforeEach
        void setSendCertificateDto() {
            sendCodeDto = new SendCodeDto(user.getEmail());
        }

        @DisplayName("사용자 이메일로 인증 코드를 보내는데 성공한다")
        @Test
        void success() {
            // given // when
            certificateService.sendCertificate(sendCodeDto);

            // then
            smtp.waitForIncomingEmail(30 * 1000L, 1);

            String actualCode = GreenMailUtil.getBody(smtp.getReceivedMessages()[0]);
            Certificate certificate = certificateRedisRepository.findById(user.getEmail())
                .orElseThrow();

            assertThat(actualCode).isEqualTo(certificate.getCode());
        }

        @DisplayName("이미 인증된 사용자의 경우 예외가 발생한다")
        @Test
        void failAlreadyCertified() {
            // given
            user.certify();
            userRepository.save(user);

            // when
            ThrowingCallable sendCertificate = () -> certificateService.sendCertificate(sendCodeDto);

            // then
            assertThatExceptionOfType(AlreadyCertifiedUserException.class)
                .isThrownBy(sendCertificate);
        }

    }

    @DisplayName("코드 인증 테스트")
    @Nested
    class CertifyTest {

        CertifyDto certifyDto;
        Certificate certificate;

        @BeforeEach
        void setup() {
            certifyDto = new CertifyDto(user.getEmail(), RandomStringUtils.randomAlphanumeric(Certificate.SIZE));
            certificate = Certificate.builder()
                .id(certifyDto.email())
                .code(certifyDto.code())
                .build();
        }

        @DisplayName("사용자 이메일 인증에 성공한다")
        @Test
        void success() {
            // given
            certificateRedisRepository.save(certificate);

            // when
            certificateService.certify(certifyDto);

            // then
            User actualUser = userRepository.findById(user.getId())
                .orElseThrow();

            assertThat(actualUser.getCertified()).isTrue();
        }

        @DisplayName("존재하지 않는 사용자에 대한 인증 요청은 예외가 발생한다")
        @Test
        void failUserNotFound() {
            // given
            String email = FAKER.animal().name() + "." + user.getEmail();
            CertifyDto notExistUserIdDto = new CertifyDto(email, certifyDto.code());

            // when
            ThrowingCallable certify = () -> certificateService.certify(notExistUserIdDto);

            // then
            assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(certify)
                .withMessage(UserNotFoundException.DEFAULT_MESSAGE);
        }

        @DisplayName("이미 인증된 사용자에 대한 인증 요청 시 예외가 발생한다")
        @Test
        void failAlreadyCertified() {
            // given
            user.certify();
            userRepository.save(user);

            // when
            ThrowingCallable certify = () -> certificateService.certify(certifyDto);

            // then
            assertThatExceptionOfType(AlreadyCertifiedUserException.class)
                .isThrownBy(certify);
        }

        @DisplayName("인증 코드가 존재하지 않을 시 예외가 발생한다.")
        @Test
        void failCertificateNotFound() {
            // given // when
            ThrowingCallable certify = () -> certificateService.certify(certifyDto);

            // then
            assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(certify);
        }

        @DisplayName("인증 코드가 다를 경우 예외가 발생한다")
        @Test
        void failIncorrectCertificate() {
            // given
            certificateRedisRepository.save(certificate);

            String incorrectCode = RandomStringUtils.random(Certificate.SIZE);
            CertifyDto incorrectCertificateDto = new CertifyDto(user.getEmail(), incorrectCode);

            // when
            ThrowingCallable certify = () -> certificateService.certify(incorrectCertificateDto);

            // then
            assertThatExceptionOfType(IncorrectCertificateException.class)
                .isThrownBy(certify)
                .withMessage(IncorrectCertificateException.DEFAULT_MESSAGE);
        }

    }

}
