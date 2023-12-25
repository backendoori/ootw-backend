package com.backendoori.ootw.domain;

import com.backendoori.ootw.dto.WeatherInfo;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    protected Weather(Double currentTemperature, Double dayMaxTemperature, Double dayMinTemperature, SkyType skyType,
                      PtyType ptyType) {
        this.currentTemperature = new Temperature(currentTemperature);
        this.dayMinTemperature = new Temperature(dayMinTemperature);
        this.dayMaxTemperature = new Temperature(dayMaxTemperature);
        this.skyType = skyType;
        this.ptyType = ptyType;
    }

    public static Weather from(WeatherInfo weatherInfo) {
        return new Weather(
            weatherInfo.currentTemperature(),
            weatherInfo.dayMaxTemperature(),
            weatherInfo.dayMinTemperature(),
            SkyType.getByCode(weatherInfo.skyCode()),
            PtyType.getByCode(weatherInfo.ptyCode())
        );
    }

}
