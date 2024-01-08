package com.backendoori.ootw.weather.controller;

import com.backendoori.ootw.weather.dto.WeatherResponse;
import com.backendoori.ootw.weather.service.WeatherService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/weather")
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping
    public ResponseEntity<WeatherResponse> readCurrentWeather(
        @Min(0)
        @Max(999)
        @NotNull
        @RequestParam
        int nx,
        @Min(0)
        @Max(999)
        @NotNull
        @RequestParam
        int ny) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(weatherService.getCurrentWeather(nx, ny));
    }

}
