package com.backendoori.ootw.common.validation;

import java.util.Arrays;
import java.util.List;
import com.backendoori.ootw.avatar.domain.Type;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ItemTypeValidator implements ConstraintValidator<ItemTypeValid, String> {

    private ItemTypeValid annotation;

    @Override
    public void initialize(ItemTypeValid constraintAnnotation) {
        this.annotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(String type, ConstraintValidatorContext context) {
        if (type == null) {
            return false;
        }
        List<String> enumList = Arrays.stream(Type.class.getEnumConstants())
            .map(Enum::name)
            .toList();
        if (!enumList.contains(type)) {
            return false;
        }

        return true;
    }

}
