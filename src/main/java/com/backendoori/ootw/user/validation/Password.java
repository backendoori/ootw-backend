package com.backendoori.ootw.user.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {

    int MIN_SIZE = 8;
    int MAX_SIZE = 30;
    String PATTERN = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%%*#?&])[A-Za-z[0-9]$@$!%%*#?&]" +
        "{" + MIN_SIZE + "," + MAX_SIZE + "}$";

    String message() default Message.INVALID_PASSWORD;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
