package com.backendoori.ootw.post.dto;

import static com.backendoori.ootw.post.validation.Message.BLANK_POST_CONTENT;
import static com.backendoori.ootw.post.validation.Message.BLANK_POST_TITLE;

import com.backendoori.ootw.weather.domain.Coordinate;
import com.backendoori.ootw.weather.validation.Grid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostSaveRequest(
    @NotBlank(message = BLANK_POST_TITLE)
    @Size(max = 30)
    String title,

    @NotBlank(message = BLANK_POST_CONTENT)
    @Size(max = 500)
    String content,

    @Grid
    Coordinate coordinate
) {

}
