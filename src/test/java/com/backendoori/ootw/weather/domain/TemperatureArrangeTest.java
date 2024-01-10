package com.backendoori.ootw.weather.domain;

import static com.backendoori.ootw.weather.validation.Message.CAN_NOT_USE_FORECAST_API;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import com.backendoori.ootw.weather.domain.forecast.ForecastCategory;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class TemperatureArrangeTest {

    private static Stream<HashMap<ForecastCategory, String>> provideInvalidWeatherInfoMap() {
        HashMap<ForecastCategory, String> weatherInfoMapWithOnlyTmn = new HashMap<>();
        weatherInfoMapWithOnlyTmn.put(ForecastCategory.TMN, String.valueOf(0.0));

        HashMap<ForecastCategory, String> weatherInfoMapWithOnlyTmx = new HashMap<>();
        weatherInfoMapWithOnlyTmx.put(ForecastCategory.TMX, String.valueOf(15.0));

        HashMap<ForecastCategory, String> weatherInfoMapWithNoData = new HashMap<>();

        return Stream.of(
            weatherInfoMapWithOnlyTmn,
            weatherInfoMapWithOnlyTmx,
            weatherInfoMapWithNoData
        );
    }

    @Test
    @DisplayName("TMN, TMX가 포함된 결과 맵(map)으로부터 TemperatureArrange 생성에 성공한다.")
    void createTemperatureArrangeSuccess() {
        // given
        HashMap<ForecastCategory, String> weatherInfoMap = new HashMap<>();
        Temperature minTemperature = Temperature.of(0.0);
        weatherInfoMap.put(ForecastCategory.TMN, String.valueOf(minTemperature.getValue()));

        Temperature maxTemperature = Temperature.of(0.0);
        weatherInfoMap.put(ForecastCategory.TMX, String.valueOf(maxTemperature.getValue()));

        // when // then
        TemperatureArrange temperatureArrange = TemperatureArrange.from(weatherInfoMap);
        assertAll(
            () -> assertThat(temperatureArrange).hasFieldOrPropertyWithValue("min", minTemperature),
            () -> assertThat(temperatureArrange).hasFieldOrPropertyWithValue("max", maxTemperature)
        );

    }

    @ParameterizedTest
    @MethodSource("provideInvalidWeatherInfoMap")
    @DisplayName("TMN, TMX가 포함되지 않은 결과 맵(map)으로부터 TemperatureArrange 생성에 실패한다.")
    void createTemperatureArrangeFail(Map<ForecastCategory, String> weatherInfoMap) {
        // given // when
        ThrowingCallable createTemperatureArrange = () -> TemperatureArrange.from(weatherInfoMap);

        // then
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(createTemperatureArrange)
            .withMessage(CAN_NOT_USE_FORECAST_API);
    }

}
