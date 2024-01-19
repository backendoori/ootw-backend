package com.backendoori.ootw.weather.util.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "forecast-api", url = "${openfeign.forecast.url}")
public interface ForecastApi {

    @GetMapping(value = "/getVilageFcst")
    String getVillageForecast(@RequestParam String serviceKey,
                              @RequestParam int numOfRows,
                              @RequestParam int pageNo,
                              @RequestParam String dataType,
                              @RequestParam(name = "base_date") String baseDate,
                              @RequestParam(name = "base_time") String baseTime,
                              @RequestParam int nx,
                              @RequestParam int ny
    );

    @GetMapping(value = "/getUltraSrtFcst")
    String getUltraShortForecast(@RequestParam String serviceKey,
                                 @RequestParam int numOfRows,
                                 @RequestParam int pageNo,
                                 @RequestParam String dataType,
                                 @RequestParam(name = "base_date") String baseDate,
                                 @RequestParam(name = "base_time") String baseTime,
                                 @RequestParam int nx,
                                 @RequestParam int ny
    );

}
