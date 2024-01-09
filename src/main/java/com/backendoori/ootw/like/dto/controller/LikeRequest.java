package com.backendoori.ootw.like.dto.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record LikeRequest(
    @Positive
    @NotNull(message = NULL_MESSAGE)
    Long postId
) {

    private static final String NULL_MESSAGE = "반드시 postId 값이 null 입니다.";

}
