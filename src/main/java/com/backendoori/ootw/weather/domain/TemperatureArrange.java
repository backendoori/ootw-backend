package com.backendoori.ootw.weather.domain;

import java.util.Map;
import com.backendoori.ootw.weather.domain.forecast.ForecastCategory;
import com.backendoori.ootw.weather.dto.TemperatureArrangeDto;
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

    // TODO: dto에서 생성 vs  vs ModelMapper 사용
    public static TemperatureArrange from(TemperatureArrangeDto weatherDto) {
        Temperature minTemperature = Temperature.of(weatherDto.min());
        Temperature maxTemperature = Temperature.of(weatherDto.max());
        validateTemperatureArrange(minTemperature, maxTemperature);

        return new TemperatureArrange(minTemperature, maxTemperature);
    }

    public static TemperatureArrange from(Map<ForecastCategory, String> temperatureArrange) {
        Temperature dayMinTemperature =
            Temperature.of(Double.parseDouble(temperatureArrange.get(ForecastCategory.TMN)));
        Temperature dayMaxTemperature =
            Temperature.of(Double.parseDouble(temperatureArrange.get(ForecastCategory.TMX)));

        return new TemperatureArrange(dayMinTemperature, dayMaxTemperature);
    }

    private static void validateTemperatureArrange(Temperature dayMinTemperature,
                                                   Temperature dayMaxTemperature) {
        Assert.isTrue(dayMinTemperature.getValue() <= dayMaxTemperature.getValue(), "일 최저 기온, 일 최고 기온 값이 적절하지 않습니다.");
    }
}
