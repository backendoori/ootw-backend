package com.backendoori.ootw.exception;

import org.springframework.security.core.AuthenticationException;

public class PermissionException extends AuthenticationException {

    public static final String DEFAULT_MESSAGE = "요청에 대한 권한이 없습니다.";

    public PermissionException() {
        super(DEFAULT_MESSAGE);
    }

}
