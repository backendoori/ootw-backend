package com.backendoori.ootw.domain.weather;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class TemperatureTest {

    @DisplayName("온도값이 유효하지 않은 경우")
    @ParameterizedTest(name = "{0}이 온도값인 경우 Temperature 생성에 실패한다.")
    @ValueSource(doubles = {-1000.0, -900.0, 900.0, 1000.0})
    @NullSource
    void validateWithInvalidTemperature(Double value) {
        // given, when, then
        assertThrows(IllegalArgumentException.class, () -> new Temperature(value));
    }

    @DisplayName("온도값이 유효한 경우")
    @ParameterizedTest(name = "{0}이 온도값인 경우 Temperature 생성에 성공한다.")
    @ValueSource(doubles = {-899.99, -30.0, 0.0, 30.0, 899.99})
    void validateWithValidTemperature(Double value) {
        // given, when, then
        assertDoesNotThrow(() -> new Temperature(value));
    }

}
