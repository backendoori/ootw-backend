package com.backendoori.ootw.weather.exception;

import static com.backendoori.ootw.weather.exception.ForecastResultErrorManager.API_SERVER_ERROR_MESSAGE;
import static com.backendoori.ootw.weather.exception.ForecastResultErrorManager.checkResultCode;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.NoSuchElementException;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@TestInstance(Lifecycle.PER_CLASS)
class ForecastResultErrorManagerTest {

    private static Stream<Arguments> provideErrorCodeWithExceptionClass() {
        return Stream.of(
            Arguments.of("01", IllegalArgumentException.class),
            Arguments.of("03", NoSuchElementException.class),
            Arguments.of("10", IllegalArgumentException.class));
    }

    @Test
    @DisplayName("정상 응답 코드면 에러가 발생하지 않는다.")
    void checkResultNormalCodeSuccess() {
        // given
        String normalResultCode = "00";

        // when
        assertDoesNotThrow(() -> checkResultCode(normalResultCode));
    }

    @ParameterizedTest(name = "[{index}] 코드가 {0}이면 {1}가 발생한다.")
    @MethodSource("provideErrorCodeWithExceptionClass")
    @DisplayName("정의된 에러 코드이면 명시된 예외가 발생한다.")
    void checkResultErrorCodeSuccess(String errorResultCode, Class<RuntimeException> exceptionClass) {
        // given // when // then
        assertThrows(exceptionClass, () -> checkResultCode(errorResultCode));
    }

    @ParameterizedTest(name = "[{index}] 코드가 {0}이면 IllegalStateException이 발생한다.")
    @ValueSource(strings = {"02", "14", "30", "50"})
    @NullAndEmptySource
    @DisplayName("정의된 에러 코드가 아니면 IllegalStateException이 발생한다.")
    void checkResultCodeFail(String notDefinedErrorResultCode) {
        // given // when // then
        String errorMessage =
            assertThrows(IllegalStateException.class, () -> checkResultCode(notDefinedErrorResultCode))
                .getMessage();

        assertThat(errorMessage).isEqualTo(API_SERVER_ERROR_MESSAGE);
    }

}
