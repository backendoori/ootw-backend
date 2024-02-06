package com.backendoori.ootw.user.service;

import java.text.MessageFormat;
import com.backendoori.ootw.common.AssertUtil;
import com.backendoori.ootw.common.OotwMailSender;
import com.backendoori.ootw.exception.UserNotFoundException;
import com.backendoori.ootw.user.domain.Certificate;
import com.backendoori.ootw.user.domain.User;
import com.backendoori.ootw.user.dto.CertifyDto;
import com.backendoori.ootw.user.dto.SendCodeDto;
import com.backendoori.ootw.user.exception.AlreadyCertifiedUserException;
import com.backendoori.ootw.user.exception.ExpiredCertificateException;
import com.backendoori.ootw.user.exception.IncorrectCertificateException;
import com.backendoori.ootw.user.repository.CertificateRedisRepository;
import com.backendoori.ootw.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CertificateService {

    public static final String TITLE_FORMAT = "[#OOTW] 이메일 인증 코드 : {0}";

    private final OotwMailSender ootwMailSender;
    private final UserRepository userRepository;
    private final CertificateRedisRepository certificateRedisRepository;

    @Transactional
    public void sendCode(SendCodeDto sendCodeDto) {
        String email = sendCodeDto.email();

        User user = userRepository.findByEmail(email)
            .orElseThrow(UserNotFoundException::new);

        AssertUtil.throwIf(user.isCertified(), AlreadyCertifiedUserException::new);

        Certificate certificate = generateCertificate(email);
        String title = generateTitle(certificate);

        certificateRedisRepository.save(certificate);
        ootwMailSender.sendMail(email, title, certificate.getCode());
    }

    @Transactional
    public void certify(CertifyDto certifyDto) {
        User user = userRepository.findByEmail(certifyDto.email())
            .orElseThrow(UserNotFoundException::new);

        AssertUtil.throwIf(user.isCertified(), AlreadyCertifiedUserException::new);

        Certificate certificate = certificateRedisRepository.findById(certifyDto.email())
            .orElseThrow(ExpiredCertificateException::new);
        boolean isIncorrectCertificate = !certifyDto.code().equals(certificate.getCode());

        AssertUtil.throwIf(isIncorrectCertificate, IncorrectCertificateException::new);

        user.certify();
        certificateRedisRepository.delete(certificate);
    }

    private Certificate generateCertificate(String email) {
        String code = RandomStringUtils.randomAlphanumeric(Certificate.SIZE);

        return Certificate.builder()
            .id(email)
            .code(code)
            .build();
    }

    private String generateTitle(Certificate certificate) {
        return MessageFormat.format(TITLE_FORMAT, certificate.getCode());
    }

}
