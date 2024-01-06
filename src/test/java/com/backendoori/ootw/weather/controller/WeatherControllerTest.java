package com.backendoori.ootw.weather.controller;

import static com.backendoori.ootw.security.jwt.JwtAuthenticationFilter.TOKEN_HEADER;
import static com.backendoori.ootw.security.jwt.JwtAuthenticationFilter.TOKEN_PREFIX;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backendoori.ootw.security.TokenMockMvcTest;
import com.backendoori.ootw.user.domain.User;
import com.backendoori.ootw.user.repository.UserRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class WeatherControllerTest extends TokenMockMvcTest {

    static final String URL = "http://localhost:8080/api/v1/weather";
    static Faker faker = new Faker();

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        User user = userRepository.save(generateUser());
        setToken(user.getId());
    }

    @AfterAll
    void cleanup() {
        userRepository.deleteAll();
    }

    private User generateUser() {
        return User.builder()
            .id((long) faker.number().positive())
            .email(faker.internet().emailAddress())
            .password(faker.internet().password())
            .nickname(faker.internet().username())
            .image(faker.internet().url())
            .build();
    }

    @Test
    @DisplayName("현재 날씨 불러오기에 성공한다.")
    void readCurrentWeatherSuccess() throws Exception {
        // given
        Integer nx = 60;
        Integer ny = 127;

        // when
        MockHttpServletRequestBuilder requestBuilder = get(URL)
            .param("nx", String.valueOf(nx))
            .param("ny", String.valueOf(ny))
            .header(TOKEN_HEADER, TOKEN_PREFIX + token)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("기본 위치 날씨 불러오기에 성공한다.")
    void readCurrentDefaultWeatherSuccess() throws Exception {
        // given // when
        MockHttpServletRequestBuilder requestBuilder = get(URL)
            .header(TOKEN_HEADER, TOKEN_PREFIX + token)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("유효하지 않은 위치 값으로 현재 날씨 불러오기에 실패한다.")
    void readCurrentWeatherFailByIllegalLocation() throws Exception {
        // given
        Integer nx = faker.number().negative();
        Integer ny = faker.number().negative();

        // when
        MockHttpServletRequestBuilder requestBuilder = get(URL)
            .param("nx", String.valueOf(nx))
            .param("ny", String.valueOf(ny))
            .header(TOKEN_HEADER, TOKEN_PREFIX + token)
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
        Integer nx = 0;
        Integer ny = 0;

        // when
        MockHttpServletRequestBuilder requestBuilder = get(URL)
            .param("nx", String.valueOf(nx))
            .param("ny", String.valueOf(ny))
            .header(TOKEN_HEADER, TOKEN_PREFIX + token)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(requestBuilder)
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

}
