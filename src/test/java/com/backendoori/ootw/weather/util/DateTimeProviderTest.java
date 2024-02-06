package com.backendoori.ootw.weather.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DateTimeProviderTest {

    DateTimeProvider dateTimeProvider = new DateTimeProvider();

    @Test
    @DisplayName("현재 시간을 불러오는 데 성공하다.")
    void nowSuccess() {
        assertThat(dateTimeProvider.now()).isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS));
    }

}
