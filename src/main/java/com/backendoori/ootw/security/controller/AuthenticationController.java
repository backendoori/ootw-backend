package com.backendoori.ootw.security.controller;

import com.backendoori.ootw.security.dto.LoginDto;
import com.backendoori.ootw.security.dto.SignupDto;
import com.backendoori.ootw.security.dto.TokenDto;
import com.backendoori.ootw.security.dto.UserDto;
import com.backendoori.ootw.security.jwt.JwtAuthenticationFilter;
import com.backendoori.ootw.security.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody SignupDto signupDto) {
        UserDto userDto = authenticationService.signup(signupDto);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(userDto);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginDto loginDto) {
        TokenDto tokenDto = authenticationService.login(loginDto);
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.add(JwtAuthenticationFilter.TOKEN_HEADER,
            JwtAuthenticationFilter.TOKEN_PREFIX + tokenDto.token());

        return ResponseEntity.status(HttpStatus.CREATED)
            .headers(httpHeaders)
            .body(tokenDto);
    }

}
