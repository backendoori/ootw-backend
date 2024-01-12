package com.backendoori.ootw.post.exception;

import com.backendoori.ootw.exception.ErrorResponse;
import com.backendoori.ootw.post.controller.PostController;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackageClasses = PostController.class)
public class PostControllerAdvice {

    @ExceptionHandler(ResourceRequiredException.class)
    public ResponseEntity<ErrorResponse> handleNoUpdateResourceException(ResourceRequiredException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(errorResponse);
    }

}
