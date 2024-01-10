package com.backendoori.ootw.user.dto;

import com.backendoori.ootw.user.validation.RFC5322;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SendCertificateDto(
    @NotNull
    @NotBlank
    @Size(max = 255)
    @Email(regexp = RFC5322.REGEX)
    String email
) {

}
