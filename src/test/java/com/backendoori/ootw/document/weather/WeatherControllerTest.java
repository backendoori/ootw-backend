package com.backendoori.ootw.document.weather;

import static com.backendoori.ootw.document.common.ApiDocumentUtil.field;
import static com.backendoori.ootw.document.common.ApiDocumentUtil.getDocumentRequest;
import static com.backendoori.ootw.document.common.ApiDocumentUtil.getDocumentResponse;
import static com.backendoori.ootw.security.jwt.JwtAuthenticationFilter.TOKEN_HEADER;
import static com.backendoori.ootw.security.jwt.JwtAuthenticationFilter.TOKEN_PREFIX;
import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.VALID_COORDINATE;
import static com.backendoori.ootw.util.provider.ForecastApiUltraShortResponseSourceProvider.generateWeatherResponse;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backendoori.ootw.security.TokenMockMvcTest;
import com.backendoori.ootw.weather.service.WeatherService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureRestDocs
class WeatherControllerTest extends TokenMockMvcTest {

    static final String API = "/api/v1/weather";

    @MockBean
    WeatherService weatherService;

    @DisplayName("[GET] weather 200 Ok")
    @Test
    void readCurrentWeatherSuccess() throws Exception {
        // given
        setToken(1);
        given(weatherService.getCurrentWeather(VALID_COORDINATE))
            .willReturn(generateWeatherResponse());

        // when
        ResultActions actions = mockMvc.perform(get(API)
            .header(TOKEN_HEADER, TOKEN_PREFIX + token)
            .param("nx", String.valueOf(VALID_COORDINATE.nx()))
            .param("ny", String.valueOf(VALID_COORDINATE.ny()))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));

        // then
        actions.andExpect(status().isOk())
            .andDo(
                document("weather",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    requestHeaders(
                        headerWithName("Authorization").description("JWT 토큰")
                    ),
                    queryParameters(
                        parameterWithName("nx").description("사용자 X 좌표"),
                        parameterWithName("ny").description("사용자 Y 좌표")
                    ),
                    responseFields(
                        field("currentDateTime", JsonFieldType.STRING, "현재 시간"),
                        field("currentTemperature", JsonFieldType.NUMBER, "현재 온도"),
                        field("sky", JsonFieldType.STRING, "하늘 상태 코드"),
                        field("pty", JsonFieldType.STRING, "강수 상태 코드")
                    )
                )
            );
    }

}
