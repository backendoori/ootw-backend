package com.backendoori.ootw.common.image.exception;

import com.backendoori.ootw.exception.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ImageControllerAdvice {

    @ExceptionHandler(ImageException.class)
    public ResponseEntity<ErrorResponse> handleImageUploadException(ImageException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(errorResponse);
    }

    @ExceptionHandler(SaveException.class)
    public ResponseEntity<ErrorResponse> handleSaveException(SaveException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(errorResponse);
    }
}
