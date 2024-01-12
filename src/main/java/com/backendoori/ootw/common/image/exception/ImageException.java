package com.backendoori.ootw.common.image.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ImageException extends RuntimeException {

    public static final String IMAGE_UPLOAD_FAIL_MESSAGE = "이미지 업로드 중 예외가 발생했습니다.";
    public static final String IMAGE_ROLLBACK_FAIL_MESSAGE = "이미지 롤백 중 예외가 발생했습니다.";

    private final String message;

}
