package com.backendoori.ootw.common;

import static org.assertj.core.api.Assertions.assertThat;

import com.icegreen.greenmail.util.GreenMailUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

@ExtendWith(OutputCaptureExtension.class)
@SpringBootTest
class OotwMailSenderTest extends MailTest {

    @Autowired
    OotwMailSender ootwMailSender;

    @DisplayName("메일 전송에 성공한다")
    @Test
    void testSendMail() throws MessagingException {
        // given
        String title = GreenMailUtil.random();
        String body = GreenMailUtil.random();

        // when
        ootwMailSender.sendMail(SMTP_ADDRESS, title, body);

        // then
        smtp.waitForIncomingEmail(5 * 1000L, 1);

        MimeMessage[] receivedMessages = smtp.getReceivedMessages();
        MimeMessage message = receivedMessages[0];
        String actualReceiver = message.getAllRecipients()[0].toString();

        assertThat(receivedMessages).hasSize(1);
        assertThat(actualReceiver).isEqualTo(SMTP_ADDRESS);
        assertThat(GreenMailUtil.getBody(message)).isEqualTo(body);
    }

    @DisplayName("메일 전송에 실패할 경우 예외를 logging 한다")
    @Test
    void testSendMailLoggingException(CapturedOutput output) {
        // given
        String title = GreenMailUtil.random();
        String body = GreenMailUtil.random();

        // when
        ootwMailSender.sendMail("", title, body);

        // then
        smtp.waitForIncomingEmail(5 * 1000L, 1);

        MimeMessage[] receivedMessages = smtp.getReceivedMessages();

        assertThat(receivedMessages).isEmpty();
        assertThat(output.getOut())
            .contains("ERROR")
            .contains("Fail to send email");
    }

}
