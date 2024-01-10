package com.backendoori.ootw.user.controller;

import com.backendoori.ootw.user.dto.CertifyDto;
import com.backendoori.ootw.user.service.CertificateService;
import com.backendoori.ootw.user.validation.RFC5322;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    @PatchMapping("/certificate")
    public ResponseEntity<Void> sendCertificate(
        @NotNull
        @NotBlank
        @Size(max = 255)
        @Email(regexp = RFC5322.REGEX)
        String email
    ) {
        certificateService.sendCertificate(email);

        return ResponseEntity.status(HttpStatus.OK)
            .build();
    }

    @PatchMapping("/certify")
    public ResponseEntity<Void> certify(@Valid CertifyDto certifyDto) {
        certificateService.certify(certifyDto);

        return ResponseEntity.status(HttpStatus.OK)
            .build();
    }

}
