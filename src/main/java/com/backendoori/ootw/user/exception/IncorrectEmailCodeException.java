package com.backendoori.ootw.user.exception;

import org.springframework.security.core.AuthenticationException;

public class IncorrectEmailCodeException extends AuthenticationException {

    public static final String DEFAULT_MESSAGE = "이메일 인증 코드가 일치하지 않습니다.";

    public IncorrectEmailCodeException() {
        super(DEFAULT_MESSAGE);
    }

}
