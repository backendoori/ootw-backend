package com.backendoori.ootw.weather.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.stream.Stream;
import com.backendoori.ootw.weather.dto.forecast.BaseDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class BaseDateTimeCalculatorTest {

    private static Stream<Arguments> provideCurrentDateTime() {
        return Stream.of(
            Arguments.of(LocalDateTime.of(2023, 12, 25, 0, 12), "20231225", "0000"),
            Arguments.of(LocalDateTime.of(2024, 1, 8, 23, 0), "20240108", "2300"),
            Arguments.of(LocalDateTime.of(2028, 5, 30, 1, 1), "20280530", "0100"));
    }

    private static Stream<Arguments> provideRequestDateTime() {
        return Stream.of(
            Arguments.of(LocalDateTime.of(2023, 12, 25, 0, 12), "20231224", "2312"),
            Arguments.of(LocalDateTime.of(2024, 1, 8, 23, 0), "20240108", "2200"),
            Arguments.of(LocalDateTime.of(2028, 5, 30, 1, 1), "20280530", "0001"));
    }

    @ParameterizedTest(name = "[{index}] 시간이 {0}이면 일자는 {1}, 시간은 {2} 형식이 된다.")
    @MethodSource("provideCurrentDateTime")
    @DisplayName("현재시간을 baseDate/baseTime 형식으로 바꿔준다.")
    void getCurrentBaseDateTime(LocalDateTime localDateTime, String baseDate, String baseTime) {
        // given
        BaseDateTime baseDateTime = new BaseDateTime(baseDate, baseTime);

        // when // then
        assertThat(BaseDateTimeCalculator.getCurrentBaseDateTime(localDateTime)).isEqualTo(baseDateTime);
    }

    @ParameterizedTest(name = "[{index}] 시간이 {0}이면 일자는 {1}, 시간은 {2} 형식이 된다.")
    @MethodSource("provideRequestDateTime")
    @DisplayName("현재시간을 baseDate/baseTime 형식으로 바꿔주고, 초단기예보 발표 시간에 맞게 계산이 된다.")
    void getRequestBaseDateTime(LocalDateTime localDateTime, String baseDate, String baseTime) {
        // given
        BaseDateTime baseDateTime = new BaseDateTime(baseDate, baseTime);

        // when // then
        assertThat(BaseDateTimeCalculator.getUltraShortForecastRequestBaseDateTime(localDateTime)).isEqualTo(
            baseDateTime);
    }

}
