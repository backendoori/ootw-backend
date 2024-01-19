package com.backendoori.ootw.user.exception;

import org.springframework.security.core.AuthenticationException;

public class IncorrectPasswordException extends AuthenticationException {

    public static final String DEFAULT_MESSAGE = "비밀번호가 일치하지 않습니다.";

    public IncorrectPasswordException() {
        super(DEFAULT_MESSAGE);
    }

}
