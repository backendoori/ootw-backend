package com.backendoori.ootw.domain.weather;

import java.util.Objects;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Temperature {

    private static final Double MIN_VALUE = -900.0;
    private static final Double MAX_VALUE = 900.0;
    private Double value;

    public static void validate(Double value) {
        if (Objects.isNull(value)) {
            throw new IllegalArgumentException("기온 값은 null이 될 수 없습니다.");
        }
        if (MIN_VALUE >= value || value >= MAX_VALUE) {
            throw new IllegalArgumentException("기온 값은 -900 이하, 900 이상이 될 수 없습니다.");
        }
    }

    public static Temperature of(Double value) {
        validate(value);
        return new Temperature(value);
    }

}
