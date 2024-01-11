package com.backendoori.ootw.post.exception;

public class NoPostPermissionException extends RuntimeException {

    public static final String DEFAULT_MESSAGE = "해당 게시글에 관한 수정/삭제 권한이 없습니다.";

    public NoPostPermissionException() {
        super(DEFAULT_MESSAGE);
    }

}
