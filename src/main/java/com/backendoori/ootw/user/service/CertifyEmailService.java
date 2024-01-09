package com.backendoori.ootw.user.service;

import java.text.MessageFormat;
import com.backendoori.ootw.common.AssertUtil;
import com.backendoori.ootw.common.OotwMailSender;
import com.backendoori.ootw.exception.UserNotFoundException;
import com.backendoori.ootw.user.domain.Certificate;
import com.backendoori.ootw.user.domain.User;
import com.backendoori.ootw.user.dto.CertifyDto;
import com.backendoori.ootw.user.exception.IncorrectEmailCodeException;
import com.backendoori.ootw.user.repository.CertificateRedisRepository;
import com.backendoori.ootw.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CertifyEmailService {

    public static final int CERTIFICATE_SIZE = 6;
    public static final long EXPIRATION = 10 * 60L;
    public static final String TITLE_PREFIX = "[#OOTW] 이메일 인증 코드 : {0}";

    private final OotwMailSender ootwMailSender;
    private final UserRepository userRepository;
    private final CertificateRedisRepository certificateRedisRepository;

    @Transactional
    public void sendCertificate(User user) {
        Certificate certificate = generateCertificate(user);
        String title = generateTitle(certificate);

        certificateRedisRepository.save(certificate);
        ootwMailSender.sendMail(user.getEmail(), title, certificate.getCode());
    }

    @Transactional
    public void certify(CertifyDto certifyDto) {
        Certificate certificate = certificateRedisRepository.findByUserId(certifyDto.userId())
            .orElseThrow(UserNotFoundException::new);
        boolean isIncorrectCode = !certifyDto.code().equals(certificate.getCode());

        AssertUtil.throwIf(isIncorrectCode, IncorrectEmailCodeException::new);

        User user = userRepository.findById(certifyDto.userId())
            .orElseThrow(UserNotFoundException::new);

        user.certify();
        certificateRedisRepository.delete(certificate);
    }

    private Certificate generateCertificate(User user) {
        String code = RandomStringUtils.randomAlphanumeric(CERTIFICATE_SIZE);

        return Certificate.builder()
            .userId(user.getId())
            .code(code)
            .expiration(EXPIRATION)
            .build();
    }

    private String generateTitle(Certificate certificate) {
        return MessageFormat.format(TITLE_PREFIX, certificate.getCode());
    }

}
