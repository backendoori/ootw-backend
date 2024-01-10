package com.backendoori.ootw.weather.controller;

import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.NO_DATA_COORDINATE;
import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.VALID_COORDINATE;
import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.VALID_NX;
import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.VALID_NY;
import static com.backendoori.ootw.util.provider.ForecastApiUltraShortResponseSourceProvider.generateWeatherResponse;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.NoSuchElementException;
import java.util.stream.Stream;
import com.backendoori.ootw.weather.domain.Coordinate;
import com.backendoori.ootw.weather.service.WeatherService;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@WithMockUser
@SpringBootTest
@AutoConfigureMockMvc
class WeatherControllerTest {

    static final String URL = "http://localhost:8080/api/v1/weather";
    static final Faker FAKER = new Faker();

    @Autowired
    MockMvc mockMvc;

    @MockBean
    WeatherService weatherService;

    static Stream<Arguments> provideInvalidCoordinate() {
        return Stream.of(
            Arguments.of(new Coordinate(VALID_NX, FAKER.number().negative())),
            Arguments.of(new Coordinate(FAKER.number().negative(), VALID_NY)),
            Arguments.of(new Coordinate(VALID_NX, FAKER.number().numberBetween(1000, 10000))),
            Arguments.of(new Coordinate(FAKER.number().numberBetween(1000, 10000), VALID_NY))
        );
    }

    @Test
    @DisplayName("현재 날씨 불러오기에 성공한다.")
    void readCurrentWeatherSuccess() throws Exception {
        // given
        given(weatherService.getCurrentWeather(VALID_COORDINATE)).willReturn(generateWeatherResponse());

        // when
        MockHttpServletRequestBuilder requestBuilder = get(URL)
            .param("nx", String.valueOf(VALID_COORDINATE.nx()))
            .param("ny", String.valueOf(VALID_COORDINATE.ny()))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("provideInvalidCoordinate")
    @DisplayName("유효하지 않은 위치 값으로 현재 날씨 불러오기에 실패한다.")
    void readCurrentWeatherFailByIllegalLocation(Coordinate invalidCoordinate) throws Exception {
        // given // when
        MockHttpServletRequestBuilder requestBuilder = get(URL)
            .param("nx", String.valueOf(invalidCoordinate.nx()))
            .param("ny", String.valueOf(invalidCoordinate.ny()))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("정보가 없는 위치 값으로 현재 날씨 불러오기에 실패한다.")
    void readCurrentWeatherFailByNoData() throws Exception {
        // given
        given(weatherService.getCurrentWeather(NO_DATA_COORDINATE))
            .willThrow(NoSuchElementException.class);

        // when
        MockHttpServletRequestBuilder requestBuilder = get(URL)
            .param("nx", String.valueOf(NO_DATA_COORDINATE.nx()))
            .param("ny", String.valueOf(NO_DATA_COORDINATE.ny()))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(requestBuilder)
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

}
