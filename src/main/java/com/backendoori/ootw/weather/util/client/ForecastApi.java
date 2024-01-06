package com.backendoori.ootw.weather.util.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "forecast-api", url = "${openfeign.forecast.url}")
public interface ForecastApi {

    @GetMapping(value = "/getVilageFcst")
    String getVillageForecast(@RequestParam String serviceKey,
                              @RequestParam Integer numOfRows,
                              @RequestParam Integer pageNo,
                              @RequestParam String dataType,
                              @RequestParam(name = "base_date") String baseDate,
                              @RequestParam(name = "base_time") String baseTime,
                              @RequestParam Integer nx,
                              @RequestParam Integer ny
    );

    @GetMapping(value = "/getUltraSrtFcst")
    String getUltraShortForecast(@RequestParam String serviceKey,
                                 @RequestParam Integer numOfRows,
                                 @RequestParam Integer pageNo,
                                 @RequestParam String dataType,
                                 @RequestParam(name = "base_date") String baseDate,
                                 @RequestParam(name = "base_time") String baseTime,
                                 @RequestParam Integer nx,
                                 @RequestParam Integer ny
    );

}
