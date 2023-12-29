package com.backendoori.ootw.exception;

import org.springframework.security.core.AuthenticationException;

public class IncorrectPasswordException extends AuthenticationException {

    public static final String DEFAULT_MESSAGE = "비밀번호가 일치하지 않습니다.";

    public IncorrectPasswordException() {
        super(DEFAULT_MESSAGE);
    }

    public IncorrectPasswordException(String msg) {
        super(msg);
    }

    public IncorrectPasswordException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
