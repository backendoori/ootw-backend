package com.backendoori.ootw.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ImageUploadException extends RuntimeException{

    private final String message = "이미지 업로드 중 예외가 발생했습니다.";

}
