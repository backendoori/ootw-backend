package com.backendoori.ootw.weather.util.client;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import com.backendoori.ootw.weather.dto.forecast.BaseDateTime;
import com.backendoori.ootw.weather.util.BaseDateTimeCalculator;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ForecastApiClientTest {

    static final Integer VALID_NX = 50;
    static final Integer VALID_NY = 127;
    static final BaseDateTime TEMP_BASE_DATETIME = BaseDateTimeCalculator.getRequestBaseDateTime(LocalDateTime.now());
    static final Faker FAKER = new Faker();

    @Autowired
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
    @DisplayName("정보가 없는 위치 값으로 현재 날씨 불러오기에 실패한다.")
    void requestUltraShortForecastItemsFailByNoSuchElementException() {
        // given
        Integer nx = 0;
        Integer ny = 0;

        // when // then
        assertThrows(NoSuchElementException.class,
            () -> forecastApiClient.requestUltraShortForecastItems(TEMP_BASE_DATETIME, nx, ny));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidRange")
    @DisplayName("유효하지 않은 파라미터 범위로 현재 날씨 불러오기에 실패한다.")
    void requestUltraShortForecastItemsFailByIllegalRange(Integer nx, Integer ny) {
        // given // when // then
        assertThrows(IllegalArgumentException.class,
            () -> forecastApiClient.requestUltraShortForecastItems(TEMP_BASE_DATETIME, nx, ny));
    }

}
