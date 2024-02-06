package com.backendoori.ootw.common.image.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SaveException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "이미지 업로드 후 저장 로직에서 예외가 발생했습니다.";

}
