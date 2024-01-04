package com.backendoori.ootw.weather.util.client;

import com.backendoori.ootw.weather.domain.ForecastResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "forecast-api", url = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0")
public interface ForecastApi {

    @GetMapping(value = "/getVilageFcst")
    ForecastResult getVillageForecast(@RequestParam String serviceKey, @RequestParam int numOfRows,
                                      @RequestParam int pageNo, @RequestParam String dataType,
                                      @RequestParam(name = "base_date") String baseDate,
                                      @RequestParam(name = "base_time") String baseTime, @RequestParam int nx,
                                      @RequestParam int ny
    );

    @GetMapping(value = "/getUltraSrtFcst")
    ForecastResult getUltraShortForecast(@RequestParam String serviceKey, @RequestParam int numOfRows,
                                         @RequestParam int pageNo, @RequestParam String dataType,
                                         @RequestParam(name = "base_date") String baseDate,
                                         @RequestParam(name = "base_time") String baseTime, @RequestParam int nx,
                                         @RequestParam int ny);

}
