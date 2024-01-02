package com.backendoori.ootw.exception;

import java.util.NoSuchElementException;

public class UserNotFoundException extends NoSuchElementException {

    public static final String DEFAULT_MESSAGE = "등록되지 않은 사용자 입니다.";

    public UserNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

}
