package com.backendoori.ootw.weather.exception;

import static com.backendoori.ootw.weather.exception.ForecastResultErrorManager.checkResultCode;
import static com.backendoori.ootw.weather.validation.Message.CAN_NOT_USE_FORECAST_API;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.NoSuchElementException;
import java.util.stream.Stream;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ForecastResultErrorManagerTest {

    private static Stream<Arguments> provideErrorCodeWithExceptionClass() {
        return Stream.of(
            Arguments.of("01", IllegalArgumentException.class, ForecastResultErrorManager.APPLICATION_ERROR.name()),
            Arguments.of("03", NoSuchElementException.class, ForecastResultErrorManager.NODATA_ERROR.name()),
            Arguments.of("10", IllegalArgumentException.class,
                ForecastResultErrorManager.INVALID_REQUEST_PARAMETER_ERROR.name())
        );
    }

    @Test
    @DisplayName("정상 응답 코드면 에러가 발생하지 않는다.")
    void checkResultNormalCodeSuccess() {
        // given
        String normalResultCode = "00";

        // when // then
        assertDoesNotThrow(() -> checkResultCode(normalResultCode));
    }

    @ParameterizedTest(name = "[{index}] 코드가 {0}이면 {1}가 발생한다.")
    @MethodSource("provideErrorCodeWithExceptionClass")
    @DisplayName("정의된 에러 코드이면 명시된 예외가 발생한다.")
    void checkResultErrorCodeSuccess(String errorResultCode, Class<RuntimeException> exceptionClass, String message) {
        // given // when
        ThrowingCallable checkResultCode = () -> checkResultCode(errorResultCode);

        // then
        assertThatExceptionOfType(exceptionClass)
            .isThrownBy(checkResultCode)
            .withMessage(message);
    }

    @ParameterizedTest(name = "[{index}] 코드가 {0}이면 IllegalStateException이 발생한다.")
    @ValueSource(strings = {"02", "14", "30", "50"})
    @NullAndEmptySource
    @DisplayName("정의된 에러 코드가 아니면 IllegalStateException이 발생한다.")
    void checkResultCodeFail(String notDefinedErrorResultCode) {
        // given // when
        ThrowingCallable checkResultCode = () -> checkResultCode(notDefinedErrorResultCode);

        // then
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(checkResultCode)
            .withMessage(CAN_NOT_USE_FORECAST_API);
    }

}
