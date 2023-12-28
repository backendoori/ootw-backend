package com.backendoori.ootw.dto;

import com.backendoori.ootw.domain.AvatarItem;

public record AvatarAppearanceResponse(
    String type,
    boolean sex,
    String url
) {

    public static AvatarAppearanceResponse from(AvatarItem avatarItem) {
        return new AvatarAppearanceResponse(avatarItem.getType().name(),
            avatarItem.isSex(),
            avatarItem.getImage());
    }

}
