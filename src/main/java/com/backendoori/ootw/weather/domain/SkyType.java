package com.backendoori.ootw.weather.domain;

import static com.backendoori.ootw.weather.validation.Message.CAN_NOT_RETRIEVE_SKYTYPE;

import java.util.Arrays;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SkyType {

    SUNNY(1),
    CLOUDY(3),
    OVERCAST(4);

    private final Integer code;

    public static SkyType getByCode(Integer code) {
        return Arrays.stream(values())
            .filter(skyType -> skyType.matchCode(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(CAN_NOT_RETRIEVE_SKYTYPE));
    }

    private boolean matchCode(Integer code) {
        return Objects.equals(this.code, code);
    }

}
