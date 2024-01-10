package com.backendoori.ootw.weather.util.client;

import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.ULTRA_SHORT_FORECAST_BASE_DATETIME;
import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.VALID_NX;
import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.VALID_NY;
import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.VILLAGE_FORECAST_BASE_DATETIME;
import static com.backendoori.ootw.util.provider.ForecastApiCommonResponseSourceProvider.FORECAST_RESPONSE_WITH_ONE_ITEM;
import static com.backendoori.ootw.util.provider.ForecastApiCommonResponseSourceProvider.INVALID_FORECAST_RESPONSE;
import static com.backendoori.ootw.util.provider.ForecastApiCommonResponseSourceProvider.INVALID_PARAMETER_FORECAST_RESPONSE;
import static com.backendoori.ootw.util.provider.ForecastApiCommonResponseSourceProvider.NO_DATA_FORECAST_RESPONSE;
import static com.backendoori.ootw.util.provider.ForecastApiCommonResponseSourceProvider.ULTRA_SHORT_FORECAST_RESPONSE_WITH_ONE_ITEM_RESPONSE;
import static com.backendoori.ootw.util.provider.ForecastApiCommonResponseSourceProvider.VILLAGE_FORECAST_RESPONSE_WITH_ONE_ITEM_RESPONSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import com.backendoori.ootw.weather.util.deserializer.ForecastResultItem;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    @Nested
    @DisplayName("현재 초단기예보 불러오기")
    class RequestUltraShortForecastItems {

        static Stream<Arguments> provideInvalidLocation() {
            return Stream.of(
                Arguments.of(VALID_NX, FAKER.number().negative()),
                Arguments.of(FAKER.number().negative(), VALID_NY),
                Arguments.of(VALID_NX, FAKER.number().numberBetween(1000, 10000)),
                Arguments.of(FAKER.number().numberBetween(1000, 10000), VALID_NY)
            );
        }

        void givenUltraShortForecastApiResponse(int nx, int ny, String response) {
            given(forecastApi.getUltraShortForecast(
                anyString(),
                anyInt(),
                anyInt(),
                anyString(),
                eq(ULTRA_SHORT_FORECAST_BASE_DATETIME.baseDate()),
                eq(ULTRA_SHORT_FORECAST_BASE_DATETIME.baseTime()),
                eq(nx),
                eq(ny)))
                .willReturn(response);
        }

        @Test
        @DisplayName("초단기예보를 불러오고 응답 파싱에 성공한다.")
        void requestUltraShortForecastItemsSuccess() {
            // given
            givenUltraShortForecastApiResponse(VALID_NX, VALID_NY,
                ULTRA_SHORT_FORECAST_RESPONSE_WITH_ONE_ITEM_RESPONSE);

            // when
            List<ForecastResultItem> resultItems = forecastApiClient
                .requestUltraShortForecastItems(ULTRA_SHORT_FORECAST_BASE_DATETIME, VALID_NX, VALID_NY);

            // then
            assertThat(resultItems).contains(FORECAST_RESPONSE_WITH_ONE_ITEM);
        }

        @Test
        @DisplayName("사용할 수 없는 응답이 오는 경우, 초단기예보를 불러오고 응답 파싱에 실패한다.")
        void requestUltraShortForecastItemsFail() {
            // given
            givenUltraShortForecastApiResponse(VALID_NX, VALID_NY, INVALID_FORECAST_RESPONSE);

            // when // then
            assertThrows(IllegalStateException.class,
                () -> forecastApiClient.requestUltraShortForecastItems(ULTRA_SHORT_FORECAST_BASE_DATETIME, VALID_NX,
                    VALID_NY));
        }

        @Test
        @DisplayName("정보가 없는 위치 값으로 현재 초단기예보 불러오기에 실패한다.")
        void requestUltraShortForecastItemsFailByNoSuchElementException() {
            // given
            int nx = 0;
            int ny = 0;

            givenUltraShortForecastApiResponse(nx, ny, NO_DATA_FORECAST_RESPONSE);

            // when // then
            assertThrows(NoSuchElementException.class,
                () -> forecastApiClient.requestUltraShortForecastItems(ULTRA_SHORT_FORECAST_BASE_DATETIME, nx, ny));
        }

        @ParameterizedTest
        @MethodSource("provideInvalidLocation")
        @DisplayName("유효하지 않은 파라미터 범위로 현재 초단기예보 불러오기에 실패한다.")
        void requestUltraShortForecastItemsFailByIllegalRange(int invalidNx, int invalidNy) {
            // given
            givenUltraShortForecastApiResponse(invalidNx, invalidNy, INVALID_PARAMETER_FORECAST_RESPONSE);

            // when // then
            assertThrows(IllegalArgumentException.class,
                () -> forecastApiClient.requestUltraShortForecastItems(ULTRA_SHORT_FORECAST_BASE_DATETIME, invalidNx,
                    invalidNy));
        }

    }

    @Nested
    @DisplayName("현재 단기예보 불러오기")
    class RequestVillageForecastItems {

        static Stream<Arguments> provideInvalidLocation() {
            return Stream.of(
                Arguments.of(VALID_NX, FAKER.number().negative()),
                Arguments.of(FAKER.number().negative(), VALID_NY),
                Arguments.of(VALID_NX, FAKER.number().numberBetween(1000, 10000)),
                Arguments.of(FAKER.number().numberBetween(1000, 10000), VALID_NY)
            );
        }

        void givenVillageForecastApiResponse(int nx, int ny, String response) {
            given(forecastApi.getVillageForecast(
                anyString(),
                anyInt(),
                anyInt(),
                anyString(),
                eq(VILLAGE_FORECAST_BASE_DATETIME.baseDate()),
                eq(VILLAGE_FORECAST_BASE_DATETIME.baseTime()),
                eq(nx),
                eq(ny)))
                .willReturn(response);
        }

        @Test
        @DisplayName("단기예보를 불러오고 응답 파싱에 성공한다.")
        void requestVillageForecastItemsSuccess() {
            // given
            givenVillageForecastApiResponse(VALID_NX, VALID_NY, VILLAGE_FORECAST_RESPONSE_WITH_ONE_ITEM_RESPONSE);

            // when
            List<ForecastResultItem> resultItems = forecastApiClient
                .requestVillageForecastItems(VILLAGE_FORECAST_BASE_DATETIME, VALID_NX, VALID_NY);

            // then
            assertThat(resultItems).contains(FORECAST_RESPONSE_WITH_ONE_ITEM);

        }

        @Test
        @DisplayName("사용할 수 없는 응답이 오는 경우, 단기예보를 불러오고 응답 파싱에 실패한다.")
        void requestVillageForecastItemsFail() {
            // given
            givenVillageForecastApiResponse(VALID_NX, VALID_NY, INVALID_FORECAST_RESPONSE);

            // when // then
            assertThrows(IllegalStateException.class,
                () -> forecastApiClient.requestVillageForecastItems(VILLAGE_FORECAST_BASE_DATETIME, VALID_NX,
                    VALID_NY));
        }

        @Test
        @DisplayName("정보가 없는 위치 값으로 현재 단기예보 불러오기에 실패한다.")
        void requestVillageForecastItemsFailByNoSuchElementException() {
            // given
            int nx = 0;
            int ny = 0;

            givenVillageForecastApiResponse(nx, ny, NO_DATA_FORECAST_RESPONSE);

            // when // then
            assertThrows(NoSuchElementException.class,
                () -> forecastApiClient.requestVillageForecastItems(VILLAGE_FORECAST_BASE_DATETIME, nx, ny));
        }

        @ParameterizedTest
        @MethodSource("provideInvalidLocation")
        @DisplayName("유효하지 않은 파라미터 범위로 현재 단기예보 불러오기에 실패한다.")
        void requestVillageForecastItemsFailByIllegalRange(int invalidNx, int invalidNy) {
            // given
            givenVillageForecastApiResponse(invalidNx, invalidNy, INVALID_PARAMETER_FORECAST_RESPONSE);

            // when // then
            assertThrows(IllegalArgumentException.class,
                () -> forecastApiClient.requestVillageForecastItems(ULTRA_SHORT_FORECAST_BASE_DATETIME, invalidNx,
                    invalidNy));
        }

    }

}
