package com.backendoori.ootw.user.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.backendoori.ootw.common.MailTest;
import com.backendoori.ootw.user.domain.Certificate;
import com.backendoori.ootw.user.domain.User;
import com.backendoori.ootw.user.repository.CertificateRedisRepository;
import com.backendoori.ootw.user.repository.UserRepository;
import com.icegreen.greenmail.util.GreenMailUtil;
import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CertifyEmailServiceTest extends MailTest {

    static final Faker FAKER = new Faker();

    @Autowired
    CertifyEmailService certifyEmailService;
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
        certificateRedisRepository.deleteAll();
        userRepository.deleteById(user.getId());
    }


    @DisplayName("사용자 이메일로 인증 코드를 보내는데 성공한다.")
    @Test
    void testSendCertificate() {
        // given // when
        certifyEmailService.sendCertificate(user);

        // then
        smtp.waitForIncomingEmail(30 * 1000L, 1);

        String actualCode = GreenMailUtil.getBody(smtp.getReceivedMessages()[0]);
        Certificate certificate = certificateRedisRepository.findByUserId(user.getId())
            .orElseThrow();

        assertThat(actualCode).isEqualTo(certificate.getCode());
    }

}
