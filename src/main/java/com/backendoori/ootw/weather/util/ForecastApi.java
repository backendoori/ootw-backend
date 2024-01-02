package com.backendoori.ootw.weather.util;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "forecast-api", url = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0")
public interface ForecastApi {

    @GetMapping(value = "/getVilageFcst")
    String getForecast(
        @RequestParam String serviceKey,
        @RequestParam int numOfRows,
        @RequestParam int pageNo,
        @RequestParam String dataType,
        @RequestParam("base_date") String baseDate,
        @RequestParam("base_time") String baseTime,
        @RequestParam int nx,
        @RequestParam int ny
    );

}
