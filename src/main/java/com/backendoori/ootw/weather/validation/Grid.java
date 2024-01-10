package com.backendoori.ootw.weather.validation;

import static com.backendoori.ootw.weather.validation.Message.INVALID_LOCATION_MESSAGE;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(value = {ElementType.PARAMETER, ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CoordinateValidator.class)
public @interface Grid {

    String message = INVALID_LOCATION_MESSAGE;

    String message() default message;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
