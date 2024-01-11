package com.backendoori.ootw.post.exception;

public class ResourceNotExistException extends RuntimeException {

    public static final String DEFAULT_MESSAGE = "해당 게시글을 생성하거나 업데이트할 리소스가 존재하지 않습니다.";

    public ResourceNotExistException() {
        super(DEFAULT_MESSAGE);
    }

}
