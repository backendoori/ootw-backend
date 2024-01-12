package com.backendoori.ootw.post.exception;

public class ResourceRequiredException extends RuntimeException {

    public static final String DEFAULT_MESSAGE = "해당 게시글을 생성하거나 업데이트할 리소스가 필요합니다.";

    public ResourceRequiredException() {
        super(DEFAULT_MESSAGE);
    }

}
