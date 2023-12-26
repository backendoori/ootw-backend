package com.backendoori.ootw.domain.weather;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

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
        Assert.isNull(value, "기온 값은 null이 될 수 없습니다.");
        Assert.isTrue(!(MIN_VALUE < value && value < MAX_VALUE), "기온 값은 -900 이하, 900 이상이 될 수 없습니다.");
    }

    public static Temperature of(Double value) {
        validate(value);
        return new Temperature(value);
    }

}
