package com.backendoori.ootw.user.service;

import com.backendoori.ootw.exception.AlreadyExistEmailException;
import com.backendoori.ootw.exception.IncorrectPasswordException;
import com.backendoori.ootw.exception.UserNotFoundException;
import com.backendoori.ootw.security.jwt.TokenProvider;
import com.backendoori.ootw.user.domain.User;
import com.backendoori.ootw.user.dto.LoginDto;
import com.backendoori.ootw.user.dto.SignupDto;
import com.backendoori.ootw.user.dto.TokenDto;
import com.backendoori.ootw.user.dto.UserDto;
import com.backendoori.ootw.user.repository.UserRepository;
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
            .password(passwordEncoder.encode(signupDto.password()))
            .nickname(signupDto.nickname())
            .build();

        userRepository.save(user);

        return UserDto.from(user);
    }

    public TokenDto login(LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.email())
            .orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(loginDto.password(), user.getPassword())) {
            throw new IncorrectPasswordException();
        }

        String token = tokenProvider.createToken(user.getId());

        return new TokenDto(token);
    }

}
