package com.backendoori.ootw.weather.domain.forecast;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@TestInstance(Lifecycle.PER_CLASS)
class ForecastCategoryTest {

    private static Stream<String> provideForecastCategoryName() {
        return Arrays.stream(ForecastCategory.values())
            .map(Enum::name);
    }

    @ParameterizedTest(name = "[{index}] ForecastCategory가 {0}이면 true를 반환한다.")
    @MethodSource("provideForecastCategoryName")
    @DisplayName("정의된 기상 타입이라면 true를 반환한다.")
    void anyMatchSuccess(String categoryName) {
        // given // when // then
        assertThat(ForecastCategory.anyMatch(categoryName)).isTrue();
    }

    @ParameterizedTest(name = "[{index}] ForecastCategory가 {0}이면 false를 반환한다.")
    @ValueSource(strings = {"TTT", "EEE", "12345"})
    @NullAndEmptySource
    @DisplayName("정의된 기상 타입이 아니면 false를 반환한다.")
    void anyMatchFail(String categoryName) {
        // given // when // then
        assertThat(ForecastCategory.anyMatch(categoryName)).isFalse();
    }

}
