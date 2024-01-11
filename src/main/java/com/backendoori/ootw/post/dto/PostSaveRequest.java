package com.backendoori.ootw.post.dto;

import static com.backendoori.ootw.post.validation.Message.BLANK_POST_CONTENT;
import static com.backendoori.ootw.post.validation.Message.BLANK_POST_TITLE;
import static com.backendoori.ootw.post.validation.Message.INVALID_POST_CONTENT;
import static com.backendoori.ootw.post.validation.Message.INVALID_POST_TITLE;

import com.backendoori.ootw.weather.domain.Coordinate;
import com.backendoori.ootw.weather.validation.Grid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostSaveRequest(
    @NotBlank(message = BLANK_POST_TITLE)
    @Size(max = 30, message = INVALID_POST_TITLE)
    String title,

    @NotBlank(message = BLANK_POST_CONTENT)
    @Size(max = 500, message = INVALID_POST_CONTENT)
    String content,

    @Grid
    Coordinate coordinate
) {

}
