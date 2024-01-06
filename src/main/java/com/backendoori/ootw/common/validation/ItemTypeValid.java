package com.backendoori.ootw.common.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(value = {ElementType.PARAMETER, ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ItemTypeValidator.class)
public @interface ItemTypeValid {

    String message = "유효하지 않은 값입니다. 다시 입력해주세요";

    String message() default message;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
