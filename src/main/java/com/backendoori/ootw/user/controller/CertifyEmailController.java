package com.backendoori.ootw.user.controller;

import com.backendoori.ootw.user.dto.CertifyDto;
import com.backendoori.ootw.user.service.CertifyEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class CertifyEmailController {

    private final CertifyEmailService certifyEmailService;

    @PostMapping("/certify")
    public ResponseEntity<Void> login(@RequestParam CertifyDto certifyDto) {
        certifyEmailService.certify(certifyDto);

        return ResponseEntity.status(HttpStatus.OK)
            .build();
    }

}
