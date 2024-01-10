package com.backendoori.ootw.weather.service;

import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.DATETIME;
import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.NO_DATA_NX;
import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.NO_DATA_NY;
import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.ULTRA_SHORT_FORECAST_BASE_DATETIME;
import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.VALID_NX;
import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.VALID_NY;
import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.VILLAGE_FORECAST_BASE_DATETIME;
import static com.backendoori.ootw.util.provider.ForecastApiUltraShortResponseSourceProvider.VALID_ULTRA_SHORT_FORECAST_ITEMS;
import static com.backendoori.ootw.util.provider.ForecastApiUltraShortResponseSourceProvider.generateUltraShortWeatherInfoMap;
import static com.backendoori.ootw.util.provider.ForecastApiVillageResponseSourceProvider.VALID_VILLAGE_FORECAST_ITEMS;
import static com.backendoori.ootw.util.provider.ForecastApiVillageResponseSourceProvider.generateVillageWeatherInfoMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;

import java.util.Map;
import java.util.NoSuchElementException;
import com.backendoori.ootw.weather.domain.TemperatureArrange;
import com.backendoori.ootw.weather.domain.forecast.ForecastCategory;
import com.backendoori.ootw.weather.dto.WeatherResponse;
import com.backendoori.ootw.weather.util.DateTimeProvider;
import com.backendoori.ootw.weather.util.client.ForecastApiClient;
import net.datafaker.Faker;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class WeatherServiceTest {

    static final Faker FAKER = new Faker();
    static final int invalidNx = FAKER.number().negative();
    static final int invalidNy = FAKER.number().negative();

    @MockBean
    DateTimeProvider dateTimeProvider;

    @MockBean
    ForecastApiClient forecastApiClient;

    @Autowired
    WeatherService weatherService;

    @BeforeEach
    void setup() {
        given(dateTimeProvider.now()).willReturn(DATETIME);
    }

    @Nested
    @DisplayName("현재 날씨 정보 조회")
    class GetCurrentWeatherTest {

        @Test
        @DisplayName("유효한 위치 정보로 현재 날씨 정보 조회에 성공한다.")
        void getCurrentWeatherSuccess() {
            // given
            Map<ForecastCategory, String> weatherInfoMap = generateUltraShortWeatherInfoMap();
            WeatherResponse expectedResponse = WeatherResponse.from(DATETIME, VALID_NX, VALID_NY, weatherInfoMap);

            given(forecastApiClient.requestUltraShortForecastItems(ULTRA_SHORT_FORECAST_BASE_DATETIME, VALID_NX,
                VALID_NY))
                .willReturn(VALID_ULTRA_SHORT_FORECAST_ITEMS);

            // when
            WeatherResponse response = weatherService.getCurrentWeather(VALID_NX, VALID_NY);

            // then
            assertThat(response).isEqualTo(expectedResponse);

        }

        @Test
        @DisplayName("위치에 해당하는 날씨 데이터가 없어서 현재 날씨 정보 조회에 실패한다.")
        void getCurrentWeatherFailWithNoData() {
            // given
            given(forecastApiClient.requestUltraShortForecastItems(ULTRA_SHORT_FORECAST_BASE_DATETIME, NO_DATA_NX,
                NO_DATA_NY))
                .willThrow(NoSuchElementException.class);

            // when
            ThrowingCallable requestCurrentWeather = () -> weatherService.getCurrentWeather(NO_DATA_NX, NO_DATA_NY);

            // then
            assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(requestCurrentWeather);
        }

        @Test
        @DisplayName("파라미터 값이 유효하지 않아 현재 날씨 정보 조회에 실패한다.")
        void getCurrentWeatherFailWithInvalidParameter() {
            // given
            given(forecastApiClient.requestUltraShortForecastItems(ULTRA_SHORT_FORECAST_BASE_DATETIME, invalidNx,
                invalidNy))
                .willThrow(IllegalArgumentException.class);

            // when
            ThrowingCallable requestCurrentWeather = () -> weatherService.getCurrentWeather(invalidNx, invalidNy);

            // then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(requestCurrentWeather);
        }

        @Test
        @DisplayName("기상청 API에서 기타 오류가 발생한 경우 현재 날씨 정보 조회에 실패한다.")
        void getCurrentWeatherFailWithApiServerError() {
            // given
            given(forecastApiClient.requestUltraShortForecastItems(ULTRA_SHORT_FORECAST_BASE_DATETIME, VALID_NX,
                VALID_NY))
                .willThrow(IllegalStateException.class);

            // when
            ThrowingCallable requestCurrentWeather = () -> weatherService.getCurrentWeather(VALID_NX, VALID_NY);

            // then
            assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(requestCurrentWeather);
        }

    }

    @Nested
    @DisplayName("오늘의 일교차 조회")
    class GetCurrentTemperatureArrangeTest {

        @Test
        @DisplayName("유효한 위치 정보로 오늘의 일교차 정보 조회에 성공한다.")
        void getCurrentTemperatureArrangeSuccess() {
            // given
            Map<ForecastCategory, String> weatherInfoMap = generateVillageWeatherInfoMap();
            TemperatureArrange expectedTemperatureArrange = TemperatureArrange.from(weatherInfoMap);

            given(forecastApiClient.requestVillageForecastItems(VILLAGE_FORECAST_BASE_DATETIME, VALID_NX, VALID_NY))
                .willReturn(VALID_VILLAGE_FORECAST_ITEMS);

            // when
            TemperatureArrange temperatureArrange = weatherService.getCurrentTemperatureArrange(VALID_NX, VALID_NY);

            // then
            assertThat(temperatureArrange).isEqualTo(expectedTemperatureArrange);
        }

        @Test
        @DisplayName("위치에 해당하는 날씨 데이터가 없어서 오늘의 일교차 정보 조회에 실패한다.")
        void getCurrentTemperatureArrangeFailWithNoData() {
            // given
            given(forecastApiClient.requestVillageForecastItems(VILLAGE_FORECAST_BASE_DATETIME, NO_DATA_NX,
                NO_DATA_NY))
                .willThrow(NoSuchElementException.class);

            // when
            ThrowingCallable requestCurrentTemperatureArrange = () ->
                weatherService.getCurrentTemperatureArrange(NO_DATA_NX, NO_DATA_NY);

            // then
            assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(requestCurrentTemperatureArrange);
        }

        @Test
        @DisplayName("파라미터 값이 유효하지 않아 오늘의 일교차 정보 조회에 실패한다.")
        void getCurrentTemperatureArrangeFailWithInvalidParameter() {
            // given
            given(
                forecastApiClient.requestVillageForecastItems(VILLAGE_FORECAST_BASE_DATETIME, invalidNx, invalidNy))
                .willThrow(IllegalArgumentException.class);

            // when
            ThrowingCallable requestCurrentTemperatureArrange = () ->
                weatherService.getCurrentTemperatureArrange(invalidNx, invalidNy);

            // then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(requestCurrentTemperatureArrange);
        }

        @Test
        @DisplayName("기상청 API에서 기타 오류가 발생한 경우 오늘의 일교차 정보 조회에 실패한다.")
        void getCurrentTemperatureArrangeFailWithApiServerError() {
            // given
            given(forecastApiClient.requestVillageForecastItems(VILLAGE_FORECAST_BASE_DATETIME, VALID_NX, VALID_NY))
                .willThrow(IllegalStateException.class);

            // when
            ThrowingCallable
                requestCurrentTemperatureArrange = () ->
                weatherService.getCurrentTemperatureArrange(VALID_NX, VALID_NY);

            // then
            assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(requestCurrentTemperatureArrange);
        }

    }

}
