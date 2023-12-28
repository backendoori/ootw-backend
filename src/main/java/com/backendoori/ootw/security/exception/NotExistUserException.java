package com.backendoori.ootw.security.exception;

import org.springframework.security.core.AuthenticationException;

public class NotExistUserException extends AuthenticationException {

    public static final String DEFAULT_MESSAGE = "등록되지 않은 사용자 입니다.";

    public NotExistUserException() {
        super(DEFAULT_MESSAGE);
    }

    public NotExistUserException(String msg) {
        super(msg);
    }

    public NotExistUserException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
