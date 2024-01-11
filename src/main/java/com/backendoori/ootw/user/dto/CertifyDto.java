package com.backendoori.ootw.user.dto;

import com.backendoori.ootw.user.domain.Certificate;
import com.backendoori.ootw.user.validation.RFC5322;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CertifyDto(
    @NotBlank
    @Size(max = 255)
    @Email(regexp = RFC5322.REGEX)
    String email,

    @NotBlank
    @Pattern(regexp = Certificate.REGEX)
    String code
) {

}
