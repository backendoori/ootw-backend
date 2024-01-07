package com.backendoori.ootw.user.validation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Message {

    public static final String INVALID_EMAIL = "이메일 형식이 올바르지 않습니다.";

    public static final String INVALID_PASSWORD = "비밀번호는 숫자, 영문자, 특수문자를 포함한 "
        + PasswordValidator.MIN_SIZE + "자 이상, "
        + PasswordValidator.MAX_SIZE + "자 이내의 문자여야 합니다.";

    public static final String BLANK_PASSWORD = "비밀번호는 공백일 수 없습니다.";
    public static final String BLANK_NICKNAME = "닉네임은 공백일 수 없습니다.";

}
