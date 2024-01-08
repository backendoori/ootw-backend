package com.backendoori.ootw.user.validation;

import java.util.Objects;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (Objects.isNull(password) || password.isBlank()) {
            return violateWithMessage(context, Message.BLANK_PASSWORD);
        }

        if (!password.matches(Password.REGEX)) {
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
