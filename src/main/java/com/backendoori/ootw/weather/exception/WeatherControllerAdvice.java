package com.backendoori.ootw.weather.exception;

import com.backendoori.ootw.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("com.backendoori.ootw.weather")
public class WeatherControllerAdvice {

    @ExceptionHandler({IllegalArgumentException.class, IllegalAccessException.class})
    public ResponseEntity<ErrorResponse> handleExcaption(RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(errorResponse);
    }

    @ExceptionHandler({IllegalStateException.class, Exception.class})
    public ResponseEntity<ErrorResponse> handleIllegalStateException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(errorResponse);
    }

}
