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

    private static final String IMAGE_RELATED_EXCEPTION = "업로드 요청 중 문제가 발생했습니다.";

    @ExceptionHandler(ImageException.class)
    public ResponseEntity<ErrorResponse> handleImageUploadException(ImageException e) {
        log.error("error message : {}", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(IMAGE_RELATED_EXCEPTION);

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(errorResponse);
    }

    @ExceptionHandler(SaveException.class)
    public ResponseEntity<ErrorResponse> handleSaveException(SaveException e) {
        log.error("error message : {}", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(IMAGE_RELATED_EXCEPTION);

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(errorResponse);
    }
}
