package com.backendoori.ootw.avatar.dto;

import com.backendoori.ootw.avatar.domain.ItemType;
import com.backendoori.ootw.common.validation.Enum;
import jakarta.validation.constraints.NotNull;

public record AvatarItemRequest(
    @Enum(enumClass = ItemType.class)
    String type,
    @NotNull
    boolean sex
) {

}
