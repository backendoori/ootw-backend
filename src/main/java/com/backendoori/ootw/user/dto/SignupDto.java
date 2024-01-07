package com.backendoori.ootw.user.dto;

public record SignupDto(
    String email,
    String password,
    String nickname
) {

}
