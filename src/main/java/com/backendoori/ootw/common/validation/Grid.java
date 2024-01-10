package com.backendoori.ootw.common.validation;

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

    String message = "위치 좌표값이 유효하지 않습니다.";

    String message() default message;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
