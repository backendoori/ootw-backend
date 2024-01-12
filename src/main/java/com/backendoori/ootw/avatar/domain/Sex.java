package com.backendoori.ootw.avatar.domain;

import java.util.Arrays;

public enum Sex {
    MALE, FEMALE;

    public static boolean checkValue(String sex) {
        return Arrays.stream(Sex.values())
            .anyMatch(e -> e.name().equals(sex));
    }

}
