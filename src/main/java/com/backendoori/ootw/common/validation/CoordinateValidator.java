package com.backendoori.ootw.common.validation;

import java.util.Objects;
import com.backendoori.ootw.weather.domain.Coordinate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CoordinateValidator implements ConstraintValidator<Grid, Coordinate> {

    public static final int MAX_COORDINATE = 999;
    public static final int MIN_COORDINATE = 0;

    @Override
    public boolean isValid(Coordinate coordinate, ConstraintValidatorContext context) {
        if (Objects.isNull(coordinate)) {
            return false;
        }
        return coordinate.nx() >= MIN_COORDINATE
               && MAX_COORDINATE >= coordinate.nx()
               && coordinate.ny() >= MIN_COORDINATE
               && MAX_COORDINATE >= coordinate.ny();
    }

}
