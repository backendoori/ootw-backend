package com.backendoori.ootw.avatar.dto;

import com.backendoori.ootw.avatar.domain.AvatarItem;

public record AvatarItemResponse(
    String type,
    boolean sex,
    String url
) {

    public static AvatarItemResponse from(AvatarItem avatarItem) {
        return new AvatarItemResponse(avatarItem.getItemType().name(),
            avatarItem.isSex(),
            avatarItem.getImage());
    }

}
