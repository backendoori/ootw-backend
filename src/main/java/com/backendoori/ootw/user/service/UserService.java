package com.backendoori.ootw.user.service;

import java.util.Objects;
import com.backendoori.ootw.common.AssertUtil;
import com.backendoori.ootw.user.exception.AlreadyExistEmailException;
import com.backendoori.ootw.user.exception.IncorrectPasswordException;
import com.backendoori.ootw.exception.UserNotFoundException;
import com.backendoori.ootw.security.jwt.TokenProvider;
import com.backendoori.ootw.user.domain.User;
import com.backendoori.ootw.user.dto.LoginDto;
import com.backendoori.ootw.user.dto.SignupDto;
import com.backendoori.ootw.user.dto.TokenDto;
import com.backendoori.ootw.user.dto.UserDto;
import com.backendoori.ootw.user.repository.UserRepository;
import com.backendoori.ootw.user.validation.Message;
import com.backendoori.ootw.user.validation.Password;
import com.backendoori.ootw.user.validation.PasswordValidator;
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

        AssertUtil.throwIf(isAlreadyExistEmail, AlreadyExistEmailException::new);
        AssertUtil.isTrue(isValidPassword(signupDto.password()), Message.INVALID_PASSWORD);

        User user = buildUser(signupDto);

        userRepository.save(user);

        return UserDto.from(user);
    }

    public TokenDto login(LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.email())
            .orElseThrow(UserNotFoundException::new);
        boolean isIncorrectPassword = !matchPassword(loginDto.password(), user.getPassword());

        AssertUtil.throwIf(isIncorrectPassword, IncorrectPasswordException::new);

        String token = tokenProvider.createToken(user.getId());

        return new TokenDto(token);
    }

    private User buildUser(SignupDto signupDto) {
        return User.builder()
            .email(signupDto.email())
            .password(passwordEncoder.encode(signupDto.password()))
            .nickname(signupDto.nickname())
            .build();
    }

    private boolean matchPassword(String decrypted, String encrypted) {
        return passwordEncoder.matches(decrypted, encrypted);
    }

    private boolean isValidPassword(String password) {
        return Objects.nonNull(password) && password.matches(Password.PATTERN);
    }

}
