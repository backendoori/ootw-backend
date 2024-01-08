package com.backendoori.ootw.weather.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class PtyTypeTest {

    @DisplayName("강수 형태 코드가 유효하지 않은 값인 경우")
    @ParameterizedTest(name = "강수 형태 코드가 {0}인 경우 PtyType 반환에 실패한다.")
    @ValueSource(ints = {-1, 100})
    @NullSource
    void getByCodeFail(Integer code) {
        // given, when, then
        assertThrows(IllegalArgumentException.class, () -> PtyType.getByCode(code));
    }

    @DisplayName("강수 형태 코드가 유효한 값인 경우")
    @ParameterizedTest(name = "강수 형태 코드가 {0}인 경우 PtyType.{1} 반환에 성공한다.")
    @CsvSource(value = {"0:NONE", "1:RAIN", "2:RAIN_OR_SNOW", "3:SNOW", "4:SHOWER"}, delimiter = ':')
    void getByCodeSuccess(Integer code, String typeName) {
        // given, when
        PtyType retrievedType = PtyType.getByCode(code);

        // then
        assertThat(retrievedType.name()).isEqualTo(typeName);
    }

}
