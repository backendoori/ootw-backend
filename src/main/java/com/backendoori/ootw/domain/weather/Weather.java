package com.backendoori.ootw.domain.weather;

import com.backendoori.ootw.dto.WeatherInfo;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

// TODO: 저 3개의 Temperature를 여기에 넣는 것이 나은 선택인가...
@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Weather {

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "current_temperature"))
    private Temperature currentTemperature;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "day_min_temperature"))
    private Temperature dayMinTemperature;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "day_max_temperature"))
    private Temperature dayMaxTemperature;

    @Enumerated(EnumType.STRING)
    @Column(name = "sky_type")
    private SkyType skyType;

    @Enumerated(EnumType.STRING)
    @Column(name = "pty_type")
    private PtyType ptyType;

    public static Weather from(WeatherInfo weatherInfo) {
        Temperature currentTemperature = Temperature.of(weatherInfo.currentTemperature());
        Temperature dayMinTemperature = Temperature.of(weatherInfo.dayMinTemperature());
        Temperature dayMaxTemperature = Temperature.of(weatherInfo.dayMaxTemperature());
        validateCurrentAndDayTemperatures(currentTemperature, dayMinTemperature, dayMaxTemperature);
        SkyType skyType = SkyType.getByCode(weatherInfo.skyCode());
        PtyType ptyType = PtyType.getByCode(weatherInfo.ptyCode());
        return new Weather(currentTemperature, dayMinTemperature, dayMaxTemperature, skyType, ptyType);
    }

    private static void validateCurrentAndDayTemperatures(Temperature currentTemperature, Temperature dayMinTemperature,
                                                          Temperature dayMaxTemperature) {
        if (dayMaxTemperature.getValue() < dayMinTemperature.getValue()
            || dayMaxTemperature.getValue() < currentTemperature.getValue()
            || currentTemperature.getValue() < dayMinTemperature.getValue()) {
            throw new IllegalArgumentException("현재 기온, 일 최저 기온, 일 최고 기온 값이 적절하지 않습니다.");
        }
    }

}
