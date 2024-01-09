package com.backendoori.ootw.user.service;

import java.text.MessageFormat;
import com.backendoori.ootw.common.OotwMailSender;
import com.backendoori.ootw.user.dto.CertifyDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CertifyEmailService {

    public static int CERTIFICATE_SIZE = 6;
    public static String TITLE_PREFIX = "[#OOTW] 이메일 인증 코드 : {0}";

    private final OotwMailSender ootwMailSender;

    public void sendCertificate(String userEmail) {
        String certificate = generateCertificate();
        String title = generateTitle(certificate);

        ootwMailSender.sendMail(userEmail, title, certificate);
    }

    public void certify(CertifyDto certifyDto) {

    }

    private String generateCertificate() {
        return RandomStringUtils.randomAlphanumeric(CERTIFICATE_SIZE);
    }

    private String generateTitle(String certificate) {
        return MessageFormat.format(TITLE_PREFIX, certificate);
    }

}
