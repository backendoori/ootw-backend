package com.backendoori.ootw.domain.weather;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Embeddable
public class Temperature {

    private static final Double MIN_VALUE = -900.0;
    private static final Double MAX_VALUE = 900.0;
    private Double value;

    protected Temperature(Double value) {
        validate(value);
        this.value = value;
    }

    public static void validate(Double value) {
        if (MIN_VALUE >= value || value >= MAX_VALUE) {
            throw new IllegalArgumentException("온도는 -900 이하, 900 이상이 될 수 없습니다.");
        }
    }

}
