package com.backendoori.ootw.weather.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

@TestInstance(Lifecycle.PER_CLASS)
class SkyTypeTest {

    @DisplayName("하늘 상태 코드가 유효하지 않은 값인 경우")
    @ParameterizedTest(name = "하늘 상태 코드가 {0}인 경우 SkyType 반환에 실패한다.")
    @ValueSource(ints = {-1, 0, 100})
    @NullSource
    void getByCodeFail(Integer code) {
        // given, when, then
        assertThrows(IllegalArgumentException.class, () -> SkyType.getByCode(code));
    }

    @DisplayName("하늘 상태 코드가 유효한 값인 경우")
    @ParameterizedTest(name = "하늘 상태 코드가 {0}인 경우 SkyType.{1} 반환에 성공한다.")
    @CsvSource(value = {"1:SUNNY", "3:CLOUDY", "4:OVERCAST"}, delimiter = ':')
    void getByCodeSuccess(Integer code, String typeName) {
        // given, when
        SkyType retrievedType = SkyType.getByCode(code);

        // then
        assertThat(retrievedType.name()).isEqualTo(typeName);
    }

}
