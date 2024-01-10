package com.backendoori.ootw.user.service;

import com.backendoori.ootw.common.AssertUtil;
import com.backendoori.ootw.exception.UserNotFoundException;
import com.backendoori.ootw.security.jwt.TokenProvider;
import com.backendoori.ootw.user.domain.User;
import com.backendoori.ootw.user.dto.LoginDto;
import com.backendoori.ootw.user.dto.SendCertificateDto;
import com.backendoori.ootw.user.dto.SignupDto;
import com.backendoori.ootw.user.dto.TokenDto;
import com.backendoori.ootw.user.dto.UserDto;
import com.backendoori.ootw.user.exception.AlreadyExistEmailException;
import com.backendoori.ootw.user.exception.IncorrectPasswordException;
import com.backendoori.ootw.user.exception.NonCertifiedUserException;
import com.backendoori.ootw.user.repository.UserRepository;
import com.backendoori.ootw.user.validation.Message;
import com.backendoori.ootw.user.validation.Password;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserService {

    private final CertificateService certificateService;
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDto signup(SignupDto signupDto) {
        validateSignup(signupDto);

        User user = buildUser(signupDto);

        userRepository.save(user);
        certificateService.sendCertificate(new SendCertificateDto(user.getEmail()));

        return UserDto.from(user);
    }

    public TokenDto login(LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.email())
            .orElseThrow(UserNotFoundException::new);

        validateLogin(loginDto, user);

        String token = tokenProvider.createToken(user.getId());

        return new TokenDto(token);
    }

    private void validateSignup(SignupDto signupDto) {
        boolean isAlreadyExistEmail = userRepository.existsByEmail(signupDto.email());

        AssertUtil.throwIf(isAlreadyExistEmail, AlreadyExistEmailException::new);
        AssertUtil.isTrue(isValidPassword(signupDto.password()), Message.INVALID_PASSWORD);
    }

    private User buildUser(SignupDto signupDto) {
        return User.builder()
            .email(signupDto.email())
            .password(passwordEncoder.encode(signupDto.password()))
            .nickname(signupDto.nickname())
            .certified(false)
            .build();
    }

    private boolean isValidPassword(String password) {
        return StringUtils.hasLength(password) && password.matches(Password.REGEX);
    }

    private void validateLogin(LoginDto loginDto, User user) {
        AssertUtil.throwIf(!user.getCertified(), NonCertifiedUserException::new);

        boolean isIncorrectPassword = !passwordEncoder.matches(loginDto.password(), user.getPassword());

        AssertUtil.throwIf(isIncorrectPassword, IncorrectPasswordException::new);
    }

}
