package com.backendoori.ootw.avatar.domain;

import java.util.Arrays;

public enum ItemType {
    HAIR, TOP, PANTS, ACCESSORY, SHOES, BACKGROUND;

    public static boolean checkValue(String itemType) {
        return Arrays.stream(ItemType.values())
            .anyMatch(e -> e.name().equals(itemType));
    }

}
