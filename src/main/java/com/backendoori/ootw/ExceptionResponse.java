package com.backendoori.ootw;

public record ExceptionResponse<T>(
    Class exceptionClass,
    String exceptionMessage
) {

    public static <T> ExceptionResponse<T> from(T e) {
        return new ExceptionResponse<T>(e.getClass(), e.toString());
    }

}
