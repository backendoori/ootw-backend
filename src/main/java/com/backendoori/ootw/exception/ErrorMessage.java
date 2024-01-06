package com.backendoori.ootw.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage {

    JSON_CONVERT_ERROR("해당 요청을 읽을 수 없습니다.");

    private final String message;

}
