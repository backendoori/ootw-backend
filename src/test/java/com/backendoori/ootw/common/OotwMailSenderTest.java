package com.backendoori.ootw.common;

import static org.assertj.core.api.Assertions.assertThatNoException;

import jakarta.mail.MessagingException;
import net.datafaker.Faker;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OotwMailSenderTest {

    static final Faker faker = new Faker();

    @Autowired
    OotwMailSender ootwMailSender;

    @Value("${spring.mail.username}")
    String sender;

    @DisplayName("메일 전송에 성공한다")
    @Test
    void testSendMail() throws MessagingException {
        // given
        String receiver = sender;
        String title = faker.book().title();
        String body = faker.emoji().smiley();

        // when
        ThrowingCallable sendMail = () -> ootwMailSender.sendMail(receiver, title, body);

        // then
        assertThatNoException().isThrownBy(sendMail);
    }

}
