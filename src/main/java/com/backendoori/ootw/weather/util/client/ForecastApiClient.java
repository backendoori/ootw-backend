package com.backendoori.ootw.weather.util.client;

import java.util.List;
import com.backendoori.ootw.weather.domain.BaseDateTime;
import com.backendoori.ootw.weather.domain.ForecastResultItem;
import com.backendoori.ootw.weather.util.ForecastProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ForecastApiClient {

    private static final String DATA_TYPE = "JSON";
    private static final int NUM_OF_ROWS = 500;
    private static final int PAGE_NO = 1;

    private final ForecastProperties forecastProperties;
    private final ForecastApi forecastApi;

    public List<ForecastResultItem> requestUltraShortForecastItems(BaseDateTime requestBaseDateTime,
                                                                   int nx, int ny) {
        return forecastApi.getUltraShortForecast(forecastProperties.serviceKey(), NUM_OF_ROWS, PAGE_NO, DATA_TYPE,
                requestBaseDateTime.baseDate(),
                requestBaseDateTime.baseTime(), nx, ny)
            .items();
    }

}
