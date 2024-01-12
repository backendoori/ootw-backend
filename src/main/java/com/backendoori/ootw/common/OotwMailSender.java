package com.backendoori.ootw.common;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OotwMailSender {

    private static final String UTF_8 = "utf-8";

    private final JavaMailSender javaMailSender;

    @Async("email")
    public void sendMail(String receiver, String title, String body) {
        try {
            sendMimeMessage(receiver, title, body);
        } catch (MessagingException e) {
            log.error("Fail to send email, receiver: {} title: {}, body: {}", receiver, title, body, e);
        }
    }

    private void sendMimeMessage(String receiver, String title, String body) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, UTF_8);

        helper.setTo(receiver);
        helper.setSubject(title);
        helper.setText(body, true);

        javaMailSender.send(mimeMessage);
    }

}
