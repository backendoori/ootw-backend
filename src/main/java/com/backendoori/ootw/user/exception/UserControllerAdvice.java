package com.backendoori.ootw.user.exception;

import com.backendoori.ootw.exception.ErrorResponse;
import com.backendoori.ootw.user.controller.CertificateController;
import com.backendoori.ootw.user.controller.UserController;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackageClasses = {UserController.class, CertificateController.class})
public class UserControllerAdvice {

    @ExceptionHandler(AlreadyCertifiedUserException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyCertifiedUserException() {
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
            .build();
    }

    @ExceptionHandler(NonCertifiedUserException.class)
    public ResponseEntity<ErrorResponse> handleNonCertifiedUserException(NonCertifiedUserException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(errorResponse);
    }

}
