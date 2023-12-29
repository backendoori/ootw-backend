package com.backendoori.ootw.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PostSaveRequest(
    @NotNull
    Long userId,

    @NotBlank
    @Size(max = 30)
    String title,

    @NotBlank
    @Size(max = 500)
    String content,

    String image,

    WeatherDto weather
) {

}
