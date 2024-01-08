package com.backendoori.ootw.post.dto;

import com.backendoori.ootw.weather.dto.TemperatureArrangeDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostSaveRequest(
    @NotBlank
    @Size(max = 30)
    String title,

    @NotBlank
    @Size(max = 500)
    String content,

    TemperatureArrangeDto weather
) {

}
