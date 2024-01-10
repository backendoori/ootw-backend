package com.backendoori.ootw.weather.util;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class DateTimeProvider {

    public LocalDateTime now() {
        return LocalDateTime.now();
    }

}
