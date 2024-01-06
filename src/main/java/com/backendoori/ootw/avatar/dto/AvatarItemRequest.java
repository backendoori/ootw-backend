package com.backendoori.ootw.avatar.dto;

import com.backendoori.ootw.common.validation.ItemTypeValid;
import jakarta.validation.constraints.NotNull;

public record AvatarItemRequest(
    @ItemTypeValid
    String type,
    @NotNull
    boolean sex
) {

}
