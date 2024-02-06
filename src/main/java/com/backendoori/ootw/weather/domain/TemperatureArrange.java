package com.backendoori.ootw.weather.domain;

import static com.backendoori.ootw.weather.validation.Message.CAN_NOT_RETRIEVE_TEMPERATURE_ARRANGE;

import java.util.Map;
import com.backendoori.ootw.weather.domain.forecast.ForecastCategory;
import com.backendoori.ootw.weather.exception.ForecastResultErrorManager;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TemperatureArrange {

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "min_temperature", nullable = false))
    private Temperature min;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "max_temperature", nullable = false))
    private Temperature max;

    public static TemperatureArrange from(Map<ForecastCategory, String> weatherInfoMap) {
        checkIncludeTemperatureArrange(weatherInfoMap);

        Temperature dayMinTemperature =
            Temperature.of(Double.parseDouble(weatherInfoMap.get(ForecastCategory.TMN)));
        Temperature dayMaxTemperature =
            Temperature.of(Double.parseDouble(weatherInfoMap.get(ForecastCategory.TMX)));

        validateArrange(dayMinTemperature, dayMaxTemperature);

        return new TemperatureArrange(dayMinTemperature, dayMaxTemperature);
    }

    private static void validateArrange(Temperature dayMinTemperature, Temperature dayMaxTemperature) {
        Assert.isTrue(dayMinTemperature.getValue() <= dayMaxTemperature.getValue(), () -> {
            throw new IllegalStateException(CAN_NOT_RETRIEVE_TEMPERATURE_ARRANGE);
        });
    }

    private static void checkIncludeTemperatureArrange(Map<ForecastCategory, String> temperatureArrangeMap) {
        Assert.isTrue(
            temperatureArrangeMap.containsKey(ForecastCategory.TMN)
                && temperatureArrangeMap.containsKey(ForecastCategory.TMX),
            () -> {
                throw ForecastResultErrorManager.getApiServerException();
            });
    }

}
