package com.backendoori.ootw.user.exception;

import org.springframework.security.core.AuthenticationException;

public class IncorrectCertificateException extends AuthenticationException {

    public static final String DEFAULT_MESSAGE = "이메일 인증 코드가 일치하지 않습니다.";

    public IncorrectCertificateException() {
        super(DEFAULT_MESSAGE);
    }

}
