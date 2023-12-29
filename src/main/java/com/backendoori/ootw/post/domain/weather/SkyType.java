package com.backendoori.ootw.post.domain.weather;

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
            .filter(skyType -> skyType.isCodeMatch(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("하늘상태 코드가 유효하지 않은 번호입니다."));
    }

    private boolean isCodeMatch(Integer code) {
        return Objects.equals(this.code, code);
    }

}
