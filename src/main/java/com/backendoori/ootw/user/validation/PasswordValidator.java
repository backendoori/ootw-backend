package com.backendoori.ootw.user.validation;

import java.util.Objects;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, String> {

    public static final int MIN_SIZE = 8;
    public static final int MAX_SIZE = 30;
    public static final String PATTERN = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%%*#?&])[A-Za-z[0-9]$@$!%%*#?&]" +
        "{" + MIN_SIZE + "," + MAX_SIZE + "}$";

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (Objects.isNull(password) || password.isBlank()) {
            return violateWithMessage(context, Message.BLANK_PASSWORD);
        }

        if (!password.matches(PATTERN)) {
            return violateWithMessage(context, Message.INVALID_PASSWORD);
        }

        return true;
    }

    private boolean violateWithMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
            .addConstraintViolation();

        return false;
    }

}
