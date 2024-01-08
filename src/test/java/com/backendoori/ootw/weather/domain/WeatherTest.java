package com.backendoori.ootw.weather.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;
import com.backendoori.ootw.weather.dto.TemperatureArrangeDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class WeatherTest {

    private static Stream<TemperatureArrangeDto> provideValidInfo() {
        return Stream.of(
            new TemperatureArrangeDto(0.0, 0.0),
            new TemperatureArrangeDto(-899.99, -899.99),
            new TemperatureArrangeDto(899.99, 899.99),
            new TemperatureArrangeDto(-10.0, 100.0)
        );
    }

    private static Stream<Arguments> provideInvalidInfo() {
        return Stream.of(
            Arguments.of("기온값이 기온 최저값보다 낮은 경우",
                new TemperatureArrangeDto(-900.0, 0.0)),
            Arguments.of("기온값이 기온 최고값보다 높은 경우",
                new TemperatureArrangeDto(0.0, 900.0)),
            Arguments.of("일 최저 기온이 일 최고 기온보다 낮은 경우",
                new TemperatureArrangeDto(0.0, -100.0))
        );
    }

    @ParameterizedTest
    @MethodSource("provideValidInfo")
    @DisplayName("from 메서드로 유효한 WeatherInfo로부터 Weather를 생성하는 것에 성공한다.")
    void createWeatherSuccess(TemperatureArrangeDto weatherDto) {
        // given // when
        TemperatureArrange createdWeather = TemperatureArrange.from(weatherDto);

        // then
        assertAll(
            () -> assertThat(createdWeather.getMinTemperature())
                .hasFieldOrPropertyWithValue("value", weatherDto.minTemperature()),
            () -> assertThat(createdWeather.getMaxTemperature())
                .hasFieldOrPropertyWithValue("value", weatherDto.maxTemperature())
        );
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("provideInvalidInfo")
    @DisplayName("from 메서드로 유효하지 않은 WeatherInfo로부터 Weather를 생성하는 것에 실패한다.")
    void createWeatherFail(String info, TemperatureArrangeDto weatherDto) {
        // given, when, then
        assertThrows(IllegalArgumentException.class,
            () -> TemperatureArrange.from(weatherDto));
    }

}
