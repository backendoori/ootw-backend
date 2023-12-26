package com.backendoori.ootw.security.controller;

import java.util.Optional;
import com.backendoori.ootw.domain.User;
import com.backendoori.ootw.security.JwtAuthenticationFilter;
import com.backendoori.ootw.security.TokenProvider;
import com.backendoori.ootw.security.dto.LoginDto;
import com.backendoori.ootw.security.dto.SignupDto;
import com.backendoori.ootw.security.dto.TokenDto;
import com.backendoori.ootw.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.email())
            .orElseThrow();

        if (!passwordEncoder.matches(loginDto.password(), user.getPassword())) {
            throw new RuntimeException();
        }

        String token = tokenProvider.createToken(user.getId());
        TokenDto tokenDto = new TokenDto(token);
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.add(JwtAuthenticationFilter.TOKEN_HEADER,
            JwtAuthenticationFilter.TOKEN_PREFIX + token);

        return new ResponseEntity<>(tokenDto, httpHeaders, HttpStatus.CREATED);
    }

    @Transactional
    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@RequestBody SignupDto signupDto) {
        Optional<User> optionalUser = userRepository.findByEmail(signupDto.email());

        if (optionalUser.isPresent()) {
            return ResponseEntity.ok().body("exist");
        }

        User user = User.builder()
            .email(signupDto.email())
            .password(passwordEncoder.encode(signupDto.password()))
            .nickname(signupDto.nickname())
            .image(signupDto.image())
            .build();

        userRepository.save(user);

        return new ResponseEntity<>(user.getId(), HttpStatus.CREATED);
    }

}
