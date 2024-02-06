package com.backendoori.ootw.user.exception;

import org.springframework.security.core.AuthenticationException;

public class NonCertifiedUserException extends AuthenticationException {

    public static final String DEFAULT_MESSAGE = "인증되지 않은 이메일 입니다.";

    public NonCertifiedUserException() {
        super(DEFAULT_MESSAGE);
    }

}
