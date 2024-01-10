package com.backendoori.ootw.weather.util.client;

import static com.backendoori.ootw.weather.util.client.ForecastApiSourceProvider.INVALID_PARAMETER_FORECAST_RESPONSE;
import static com.backendoori.ootw.weather.util.client.ForecastApiSourceProvider.NO_DATA_FORECAST_RESPONSE;
import static com.backendoori.ootw.weather.util.client.ForecastApiSourceProvider.TEMP_BASE_DATETIME;
import static com.backendoori.ootw.weather.util.client.ForecastApiSourceProvider.VALID_NX;
import static com.backendoori.ootw.weather.util.client.ForecastApiSourceProvider.VALID_NY;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import java.util.NoSuchElementException;
import java.util.stream.Stream;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class ForecastApiClientTest {

    static final Faker FAKER = new Faker();

    @MockBean
    ForecastApi forecastApi;

    @Autowired
    ForecastApiClient forecastApiClient;

    static Stream<Arguments> provideInvalidRange() {
        return Stream.of(
            Arguments.of(FAKER.number().negative(), VALID_NY),
            Arguments.of(VALID_NX, FAKER.number().negative()),
            Arguments.of(FAKER.number().numberBetween(1000, 10000), VALID_NY),
            Arguments.of(VALID_NX, FAKER.number().numberBetween(1000, 10000)));
    }

    @Test
    @DisplayName("정보가 없는 위치 값으로 현재 초단기예보 불러오기에 실패한다.")
    void requestUltraShortForecastItemsFailByNoSuchElementException() {
        // given
        int nx = 0;
        int ny = 0;

        givenForecastApiResponse(nx, ny, NO_DATA_FORECAST_RESPONSE);

        // when // then
        assertThrows(NoSuchElementException.class,
            () -> forecastApiClient.requestUltraShortForecastItems(TEMP_BASE_DATETIME, nx, ny));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidRange")
    @DisplayName("유효하지 않은 파라미터 범위로 현재 초단기예보 불러오기에 실패한다.")
    void requestUltraShortForecastItemsFailByIllegalRange(Integer nx, Integer ny) {
        // given
        givenForecastApiResponse(nx, ny, INVALID_PARAMETER_FORECAST_RESPONSE);

        // when // then
        assertThrows(IllegalArgumentException.class,
            () -> forecastApiClient.requestUltraShortForecastItems(TEMP_BASE_DATETIME, nx, ny));
    }

    @Test
    @DisplayName("정보가 없는 위치 값으로 현재 단기예보 불러오기에 실패한다.")
    void requestVillageForecastItemsFailByNoSuchElementException() {
        // given
        int nx = 0;
        int ny = 0;

        givenForecastApiResponse(nx, ny, NO_DATA_FORECAST_RESPONSE);

        // when // then
        assertThrows(NoSuchElementException.class,
            () -> forecastApiClient.requestUltraShortForecastItems(TEMP_BASE_DATETIME, nx, ny));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidRange")
    @DisplayName("유효하지 않은 파라미터 범위로 현재 단기예보 불러오기에 실패한다.")
    void requestVillageForecastItemsFailByIllegalRange(Integer nx, Integer ny) {
        // given
        givenForecastApiResponse(nx, ny, INVALID_PARAMETER_FORECAST_RESPONSE);

        // when // then
        assertThrows(IllegalArgumentException.class,
            () -> forecastApiClient.requestVillageForecastItems(TEMP_BASE_DATETIME, nx, ny));
    }

    void givenForecastApiResponse(int nx, int ny, String response) {
        given(forecastApi.getUltraShortForecast(
            anyString(),
            anyInt(),
            anyInt(),
            anyString(),
            eq(TEMP_BASE_DATETIME.baseDate()),
            eq(TEMP_BASE_DATETIME.baseTime()),
            eq(nx),
            eq(ny)))
            .willReturn(response);
    }

}
