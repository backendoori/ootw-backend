package com.backendoori.ootw.image.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(value = ElementType.PARAMETER)
@Retention(value = RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ImageAnnotationValidator.class)
public @interface Image {

    String message = "유효하지 않은 이미지를 업로드하였습니다.";

    String message() default message;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean ignoreCase() default false;

}
