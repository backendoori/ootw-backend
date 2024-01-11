package com.backendoori.ootw.user.controller;

import com.backendoori.ootw.security.jwt.JwtAuthenticationFilter;
import com.backendoori.ootw.user.dto.LoginDto;
import com.backendoori.ootw.user.dto.SignupDto;
import com.backendoori.ootw.user.dto.TokenDto;
import com.backendoori.ootw.user.service.UserService;
import jakarta.validation.Valid;
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
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody @Valid SignupDto signupDto) {
        userService.signup(signupDto);

        return ResponseEntity.status(HttpStatus.CREATED)
            .build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody @Valid LoginDto loginDto) {
        TokenDto tokenDto = userService.login(loginDto);
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.add(JwtAuthenticationFilter.TOKEN_HEADER,
            JwtAuthenticationFilter.TOKEN_PREFIX + tokenDto.token());

        return ResponseEntity.status(HttpStatus.CREATED)
            .headers(httpHeaders)
            .body(tokenDto);
    }

}
