package com.backendoori.ootw.weather.util;

import java.time.LocalDateTime;

public class CurrentDateTimeSupplier {

    public static LocalDateTime get() {
        return LocalDateTime.now();
    }

}
