package com.backendoori.ootw.security.service;

import com.backendoori.ootw.domain.User;
import com.backendoori.ootw.security.dto.LoginDto;
import com.backendoori.ootw.security.dto.SignupDto;
import com.backendoori.ootw.security.dto.TokenDto;
import com.backendoori.ootw.security.dto.UserDto;
import com.backendoori.ootw.security.exception.AlreadyExistEmailException;
import com.backendoori.ootw.security.exception.IncorrectPasswordException;
import com.backendoori.ootw.security.exception.NotExistUserException;
import com.backendoori.ootw.security.jwt.TokenProvider;
import com.backendoori.ootw.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDto signup(SignupDto signupDto) {
        boolean isAlreadyExistEmail = userRepository.findByEmail(signupDto.email())
            .isPresent();

        if (isAlreadyExistEmail) {
            throw new AlreadyExistEmailException();
        }

        User user = User.builder()
            .email(signupDto.email())
            .password(signupDto.password())
            .nickname(signupDto.nickname())
            .image(signupDto.image())
            .build();

        userRepository.save(user);

        return UserDto.from(user);
    }

    public TokenDto login(LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.email())
            .orElseThrow(NotExistUserException::new);

        if (!passwordEncoder.matches(loginDto.password(), user.getPassword())) {
            throw new IncorrectPasswordException();
        }

        String token = tokenProvider.createToken(user.getId());

        return new TokenDto(token);
    }

}
