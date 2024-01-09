package com.backendoori.ootw.post.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PostSaveRequest(
    @NotBlank
    @Size(max = 30)
    String title,

    @NotBlank
    @Size(max = 500)
    String content,

    @Min(0)
    @Max(999)
    @NotNull
    int nx,

    @Min(0)
    @Max(999)
    @NotNull
    int ny
) {

}
