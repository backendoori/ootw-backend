package com.backendoori.ootw.user.dto;

import com.backendoori.ootw.user.validation.Message;
import com.backendoori.ootw.user.validation.Password;
import com.backendoori.ootw.user.validation.RFC5322;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SignupDto(
    @NotNull
    @Email(regexp = RFC5322.REGEX)
    String email,

    @NotNull
    @Password
    String password,

    @NotNull
    @NotBlank(message = Message.BLANK_NICKNAME)
    String nickname
) {

}
