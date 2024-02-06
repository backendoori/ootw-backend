package com.backendoori.ootw.weather.domain;

import static com.backendoori.ootw.weather.validation.Message.CAN_NOT_RETRIEVE_TEMPERATURE_ARRANGE;

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
        Assert.notNull(value, CAN_NOT_RETRIEVE_TEMPERATURE_ARRANGE);
        Assert.isTrue(MIN_VALUE < value && value < MAX_VALUE, CAN_NOT_RETRIEVE_TEMPERATURE_ARRANGE);
    }

    public static Temperature of(Double value) {
        validate(value);

        return new Temperature(value);
    }

}
