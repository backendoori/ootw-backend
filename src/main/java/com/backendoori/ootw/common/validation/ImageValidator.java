package com.backendoori.ootw.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class ImageValidator implements ConstraintValidator<Image, MultipartFile> {

    @Override
    public boolean isValid(MultipartFile img, ConstraintValidatorContext context) {
        if (img == null || img.isEmpty()) {
            return false;
        }
        if (img.getSize() > 10_000_000) {
            return false;
        }
        String contentType = img.getContentType();
        if (!contentType.startsWith("image")) {
            return false;
        }

        return true;
    }

}
