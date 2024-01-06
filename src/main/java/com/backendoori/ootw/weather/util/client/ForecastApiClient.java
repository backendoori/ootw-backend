package com.backendoori.ootw.weather.util.client;

import java.util.List;
import com.backendoori.ootw.weather.domain.BaseDateTime;
import com.backendoori.ootw.weather.domain.ForecastResultHeader;
import com.backendoori.ootw.weather.domain.ForecastResultItem;
import com.backendoori.ootw.weather.domain.ForecastSuccessResultBody;
import com.backendoori.ootw.weather.exception.ForecastResultErrorManager;
import com.backendoori.ootw.weather.util.ForecastProperties;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ForecastApiClient {

    private static final int NUM_OF_ROWS = 500;
    private static final int PAGE_NO = 1;
    private static final String DATA_TYPE = "JSON";

    private final ForecastApi forecastApi;
    private final ObjectMapper objectMapper;
    private final ForecastProperties forecastProperties;

    public List<ForecastResultItem> requestUltraShortForecastItems(BaseDateTime requestBaseDateTime,
                                                                   int nx,
                                                                   int ny) {
        String response = forecastApi.getUltraShortForecast(
                forecastProperties.serviceKey(),
                NUM_OF_ROWS,
                PAGE_NO,
                DATA_TYPE,
                requestBaseDateTime.baseDate(),
                requestBaseDateTime.baseTime(),
                nx,
            ny);

        return parseForecastResult(response);
    }

    private List<ForecastResultItem> parseForecastResult(String response) {
        try {
            ForecastResultHeader header = objectMapper.readValue(response, ForecastResultHeader.class);
            ForecastResultErrorManager.checkResultCode(header.resultCode());

            return objectMapper.readValue(response, ForecastSuccessResultBody.class)
                .items();
        } catch (JacksonException e) {
            throw new IllegalStateException(ForecastResultErrorManager.API_SERVER_ERROR_MESSAGE);
        }
    }

}
