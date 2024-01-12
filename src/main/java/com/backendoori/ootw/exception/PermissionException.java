package com.backendoori.ootw.exception;

public class PermissionException extends RuntimeException {

    public static final String DEFAULT_MESSAGE = "요청에 대한 권한이 없습니다.";

    public PermissionException() {
        super(DEFAULT_MESSAGE);
    }

}
