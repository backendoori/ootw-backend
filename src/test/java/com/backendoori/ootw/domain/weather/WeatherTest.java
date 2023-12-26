package com.backendoori.ootw.domain.weather;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;
import com.backendoori.ootw.dto.WeatherInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class WeatherTest {

    private static Stream<WeatherInfo> provideValidWeatherInfo() {
        return Stream.of(
            new WeatherInfo(0.0, 0.0, 0.0, 1, 0),
            new WeatherInfo(-899.99, -899.99, -899.99, 3, 1),
            new WeatherInfo(899.99, 899.99, 899.99, 4, 2),
            new WeatherInfo(30.0, -10.0, 100.0, 1, 3)
        );
    }

    private static Stream<Arguments> provideInvalidWeatherInfo() {
        return Stream.of(
            Arguments.of("하늘 상태 코드가 유효하지 않은 경우",
                new WeatherInfo(0.0, 0.0, 0.0, 5, 1)),
            Arguments.of("강수 형태 코드가 유효하지 않은 경우",
                new WeatherInfo(0.0, 0.0, 0.0, 1, 7)),
            Arguments.of("기온값이 기온 최저값보다 낮은 경우",
                new WeatherInfo(-900.0, 0.0, 0.0, 1, 1)),
            Arguments.of("기온값이 기온 최고값보다 높은 경우",
                new WeatherInfo(0.0, 0.0, 900.0, 1, 1)),
            Arguments.of("현재 기온이 일 최저 기온보다 낮은 경우",
                new WeatherInfo(-100.0, 0.0, 0.0, 1, 1)),
            Arguments.of("현재 기온이 일 최고 기온보다 높은 경우",
                new WeatherInfo(100.0, 0.0, 0.0, 1, 1)),
            Arguments.of("일 최저 기온이 일 최고 기온보다 낮은 경우",
                new WeatherInfo(0.0, 0.0, -100.0, 1, 1))
        );
    }

    @ParameterizedTest
    @MethodSource("provideValidWeatherInfo")
    @DisplayName("from 메서드로 유효한 WeatherInfo로부터 Weather를 생성하는 것에 성공한다.")
    void createWeatherByFromWithValidValue(WeatherInfo weatherInfo) {
        // given
        SkyType weatherInfoSkyType = SkyType.getByCode(weatherInfo.skyCode());
        PtyType weatherInfoPtyType = PtyType.getByCode(weatherInfo.ptyCode());

        // when
        Weather createdWeather = Weather.from(weatherInfo);

        // then
        assertAll(
            () -> assertThat(createdWeather.getCurrentTemperature())
                .hasFieldOrPropertyWithValue("value", weatherInfo.currentTemperature()),
            () -> assertThat(createdWeather.getDayMinTemperature())
                .hasFieldOrPropertyWithValue("value", weatherInfo.dayMinTemperature()),
            () -> assertThat(createdWeather.getDayMaxTemperature())
                .hasFieldOrPropertyWithValue("value", weatherInfo.dayMaxTemperature()),
            () -> assertThat(createdWeather.getSkyType())
                .isEqualTo(weatherInfoSkyType),
            () -> assertThat(createdWeather.getPtyType())
                .isEqualTo(weatherInfoPtyType)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideInvalidWeatherInfo")
    @DisplayName("from 메서드로 유효하지 않은 WeatherInfo로부터 Weather를 생성하는 것에 실패한다.")
    void createWeatherByFromWithInvalidValue(String info, WeatherInfo weatherInfo) {

        // given, when, then
        assertThrows(IllegalArgumentException.class,
            () -> Weather.from(weatherInfo));
    }

}
