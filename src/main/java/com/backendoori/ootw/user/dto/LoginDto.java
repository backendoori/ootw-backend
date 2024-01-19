package com.backendoori.ootw.user.dto;

import com.backendoori.ootw.user.validation.Password;
import com.backendoori.ootw.user.validation.RFC5322;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginDto(
    @NotNull
    @NotBlank
    @Email(regexp = RFC5322.REGEX)
    String email,

    @NotNull
    @Password
    String password
) {

}
