package com.backendoori.ootw.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class ImageAnnotationValidator implements ConstraintValidator<Image, MultipartFile> {

    private static final String IMAGE_PREFIX = "image";
    private Image annotation;


    @Override
    public void initialize(Image constraintAnnotation) {
        this.annotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(MultipartFile img, ConstraintValidatorContext context) {
        if (this.annotation.ignoreCase()) {
            return true;
        }

        if (img.isEmpty()) {
            return false;
        }

        if (img.getSize() > 10_000_000) {
            return false;
        }

        String contentType = img.getContentType();
        if (!contentType.startsWith(IMAGE_PREFIX)) {
            return false;
        }

        return true;
    }

}
