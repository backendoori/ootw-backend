package com.backendoori.ootw.weather.dto;

import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.DATETIME;
import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.VALID_COORDINATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import com.backendoori.ootw.weather.domain.PtyType;
import com.backendoori.ootw.weather.domain.SkyType;
import com.backendoori.ootw.weather.domain.Temperature;
import com.backendoori.ootw.weather.domain.forecast.ForecastCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class WeatherResponseTest {

    private static Stream<HashMap<ForecastCategory, String>> provideInvalidWeatherInfoMap() {
        HashMap<ForecastCategory, String> weatherInfoMapWithOnlyT1h = new HashMap<>();
        weatherInfoMapWithOnlyT1h.put(ForecastCategory.T1H, String.valueOf(0.0));

        HashMap<ForecastCategory, String> weatherInfoMapWithOnlySky = new HashMap<>();
        weatherInfoMapWithOnlySky.put(ForecastCategory.SKY, String.valueOf(1));

        HashMap<ForecastCategory, String> weatherInfoMapWithOnlyPty = new HashMap<>();
        weatherInfoMapWithOnlyPty.put(ForecastCategory.PTY, String.valueOf(0));

        HashMap<ForecastCategory, String> weatherInfoMapWithNoData = new HashMap<>();

        return Stream.of(
            weatherInfoMapWithOnlyT1h,
            weatherInfoMapWithOnlySky,
            weatherInfoMapWithOnlyPty,
            weatherInfoMapWithNoData
        );
    }

    @Test
    @DisplayName("SKY, PTY, T1H가 포함된 결과 맵(map)으로부터 WeatherResponse 생성에 성공한다.")
    void createWeatherResponseSuccess() {
        // given
        HashMap<ForecastCategory, String> weatherInfoMap = new HashMap<>();

        Temperature currentTemperature = Temperature.of(0.0);
        int skyCode = SkyType.SUNNY.getCode();
        int ptyCode = PtyType.NONE.getCode();

        weatherInfoMap.put(ForecastCategory.T1H, String.valueOf(currentTemperature.getValue()));
        weatherInfoMap.put(ForecastCategory.SKY, String.valueOf(skyCode));
        weatherInfoMap.put(ForecastCategory.PTY, String.valueOf(ptyCode));

        // when // then
        WeatherResponse weatherResponse = WeatherResponse.from(DATETIME, VALID_COORDINATE, weatherInfoMap);
        assertAll(
            () -> assertThat(weatherResponse).hasFieldOrPropertyWithValue("currentTemperature",
                currentTemperature.getValue()),
            () -> assertThat(weatherResponse).hasFieldOrPropertyWithValue("sky", SkyType.getByCode(skyCode).name()),
            () -> assertThat(weatherResponse).hasFieldOrPropertyWithValue("pty", PtyType.getByCode(ptyCode).name())
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidWeatherInfoMap")
    @DisplayName("SKY, PTY, T1H가 포함되지 않은 결과 맵(map)으로부터 WeatherResponse 생성에 성공한다.")
    void createWeatherResponseFail(Map<ForecastCategory, String> weatherInfoMap) {
        // given // when // then
        assertThrows(IllegalStateException.class,
            () -> WeatherResponse.from(DATETIME, VALID_COORDINATE, weatherInfoMap));
    }

}
