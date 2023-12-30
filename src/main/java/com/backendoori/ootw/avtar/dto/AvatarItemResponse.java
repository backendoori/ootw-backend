package com.backendoori.ootw.avtar.dto;

import com.backendoori.ootw.avtar.domain.AvatarItem;

public record AvatarItemResponse(
    String type,
    boolean sex,
    String url
) {

    public static AvatarItemResponse from(AvatarItem avatarItem) {
        return new AvatarItemResponse(avatarItem.getType().name(),
            avatarItem.isSex(),
            avatarItem.getImage());
    }

}
