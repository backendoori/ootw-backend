package com.backendoori.ootw.post.domain.weather;

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
    SHOWER(4);

    private final Integer code;

    public static PtyType getByCode(Integer code) {
        return Arrays.stream(values())
            .filter(skyType -> skyType.matchCode(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("강수 형태 코드가 유효하지 않은 번호입니다."));
    }

    private boolean matchCode(Integer code) {
        return Objects.equals(this.code, code);
    }

}
