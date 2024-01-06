package com.backendoori.ootw.weather.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

@TestInstance(Lifecycle.PER_CLASS)
class TemperatureTest {

    @DisplayName("온도값이 유효하지 않은 경우")
    @ParameterizedTest(name = "{0}이 온도값인 경우 Temperature 생성에 실패한다.")
    @ValueSource(doubles = {-1000.0, -900.0, 900.0, 1000.0})
    @NullSource
    void validateTemperatureFail(Double value) {
        // given, when, then
        assertThrows(IllegalArgumentException.class, () -> Temperature.of(value));
    }

    @DisplayName("온도값이 유효한 경우")
    @ParameterizedTest(name = "{0}이 온도값인 경우 Temperature 생성에 성공한다.")
    @ValueSource(doubles = {-899.99, -30.0, 0.0, 30.0, 899.99})
    void validateTemperatureSuccess(Double value) {
        // given, when, then
        assertDoesNotThrow(() -> Temperature.of(value));
    }

}
