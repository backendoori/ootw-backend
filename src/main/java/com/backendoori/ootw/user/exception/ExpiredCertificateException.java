package com.backendoori.ootw.user.exception;

public class ExpiredCertificateException extends IllegalArgumentException {

    public static final String DEFAULT_MESSAGE = "만료된 인증 코드 입니다.";

    public ExpiredCertificateException() {
        super(DEFAULT_MESSAGE);
    }

}
