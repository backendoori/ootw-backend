package com.backendoori.ootw.security.dto;

public record SignupDto(
    String email,
    String password,
    String nickname,
    String image
) {

}
