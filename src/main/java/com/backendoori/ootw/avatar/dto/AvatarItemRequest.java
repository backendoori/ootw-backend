package com.backendoori.ootw.avatar.dto;

import com.backendoori.ootw.avatar.domain.ItemType;
import com.backendoori.ootw.avatar.domain.Sex;
import com.backendoori.ootw.common.validation.Enum;

public record AvatarItemRequest(
    @Enum(enumClass = ItemType.class)
    String type,
    @Enum(enumClass = Sex.class)
    String sex
) {

}
