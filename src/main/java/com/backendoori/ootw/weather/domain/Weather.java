package com.backendoori.ootw.weather.domain;

import com.backendoori.ootw.weather.dto.WeatherDto;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.*;
import org.springframework.util.Assert;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Weather {

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "day_min_temperature", nullable = false))
    private Temperature dayMinTemperature;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "day_max_temperature", nullable = false))
    private Temperature dayMaxTemperature;

    // TODO: dto에서 생성 vs  vs ModelMapper 사용
    public static Weather from(WeatherDto weatherDto) {
        Temperature dayMinTemperature = Temperature.of(weatherDto.dayMinTemperature());
        Temperature dayMaxTemperature = Temperature.of(weatherDto.dayMaxTemperature());
        validateCurrentAndDayTemperatures(dayMinTemperature, dayMaxTemperature);

        return new Weather(dayMinTemperature, dayMaxTemperature);
    }

    private static void validateCurrentAndDayTemperatures(Temperature dayMinTemperature,
                                                          Temperature dayMaxTemperature) {
        Assert.isTrue(dayMinTemperature.getValue() <= dayMaxTemperature.getValue(), "일 최저 기온, 일 최고 기온 값이 적절하지 않습니다.");
    }

}
