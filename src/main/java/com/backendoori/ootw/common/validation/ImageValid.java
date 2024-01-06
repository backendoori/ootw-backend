package com.backendoori.ootw.common.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(value = ElementType.PARAMETER)
@Retention(value = RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ImageValidator.class)
public @interface ImageValid {

    String message = "유효하지 않은 이미지를 업로드하였습니다. 다른 이미지를 업로드 해주세요";

    String message() default message;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
