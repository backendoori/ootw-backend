package com.backendoori.ootw;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.MethodArgumentNotValidException;

public record ExceptionResponse<T>(
    T error
) {

    public static ExceptionResponse<List<FieldErrorDetail>> from(
        MethodArgumentNotValidException e) {
        List<FieldErrorDetail> errors = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(fieldError ->
                new FieldErrorDetail(fieldError.getField(), fieldError.getDefaultMessage()))
            .toList();
        return new ExceptionResponse<>(errors);
    }

    public static <E extends Exception> ExceptionResponse<String> from(E e) {
        return new ExceptionResponse<>(e.getMessage());
    }

    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    public static class FieldErrorDetail {

        private final String field;
        private final String defaultMessage;

    }

}
