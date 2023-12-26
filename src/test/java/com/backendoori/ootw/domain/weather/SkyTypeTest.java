package com.backendoori.ootw.domain.weather;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class SkyTypeTest {

    @DisplayName("하늘 상태 코드가 유효하지 않은 값인 경우")
    @ParameterizedTest(name = "하늘 상태 코드가 {0}인 경우 SkyType 반환에 실패한다.")
    @ValueSource(ints = {-1, 0, 100})
    void getByCodeWithInvalidCode(int code) {
        assertThrows(IllegalArgumentException.class, () -> SkyType.getByCode(code));
    }

    @DisplayName("하늘 상태 코드가 유효한 값인 경우")
    @ParameterizedTest(name = "하늘 상태 코드가 {0}인 경우 SkyType.{1} 반환에 성공한다.")
    @CsvSource(value = {"1:SUNNY", "3:CLOUDY", "4:OVERCAST"}, delimiter = ':')
    void getByCodeWithValidCode(int code, String typeName) {
        SkyType retrievedType = SkyType.getByCode(code);
        assertThat(retrievedType.name()).isEqualTo(typeName);
    }

}
