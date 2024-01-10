package com.backendoori.ootw.avatar.dto;

import com.backendoori.ootw.avatar.domain.AvatarItem;

public record AvatarItemResponse(
    String type,
    String sex,
    String url
) {

    public static AvatarItemResponse from(AvatarItem avatarItem) {
        return new AvatarItemResponse(avatarItem.getItemType().name(),
            avatarItem.getSex().name(),
            avatarItem.getImage());
    }

}
