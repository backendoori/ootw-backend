package com.backendoori.ootw.user.exception;

import org.springframework.dao.DuplicateKeyException;

public class AlreadyExistEmailException extends DuplicateKeyException {

    public static final String DEFAULT_MESSAGE = "이미 사용 중인 email 입니다.";

    public AlreadyExistEmailException() {
        super(DEFAULT_MESSAGE);
    }

}
