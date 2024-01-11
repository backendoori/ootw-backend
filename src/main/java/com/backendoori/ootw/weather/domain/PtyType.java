package com.backendoori.ootw.weather.domain;

import static com.backendoori.ootw.weather.validation.Message.CAN_NOT_RETRIEVE_PTYTYPE;

import java.util.Arrays;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum PtyType {

    NONE(0),
    RAIN(1),
    RAIN_OR_SNOW(2),
    SNOW(3),
    SHOWER(4),
    RAIN_DROP(5),
    RAIN_DROP_OR_SNOW_DRIFTING(6),
    SNOW_DRIFTING(7);

    private final Integer code;

    public static PtyType getByCode(Integer code) {
        return Arrays.stream(values())
            .filter(skyType -> skyType.matchCode(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(CAN_NOT_RETRIEVE_PTYTYPE));
    }

    private boolean matchCode(Integer code) {
        return Objects.equals(this.code, code);
    }

}
