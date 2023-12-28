package com.backendoori.ootw.security.exception;

import org.springframework.security.core.AuthenticationException;

public class AlreadyExistEmailException extends AuthenticationException {

    public static final String DEFAULT_MESSAGE = "이미 사용 중인 email 입니다.";

    public AlreadyExistEmailException() {
        super(DEFAULT_MESSAGE);
    }

    public AlreadyExistEmailException(String msg) {
        super(msg);
    }

    public AlreadyExistEmailException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
