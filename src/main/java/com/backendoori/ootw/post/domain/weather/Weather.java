package com.backendoori.ootw.post.domain.weather;

import com.backendoori.ootw.post.dto.WeatherDto;
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
import org.springframework.util.Assert;

// TODO: 저 3개의 Temperature를 여기에 넣는 것이 나은 선택인가...
@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Weather {

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "current_temperature", nullable = false))
    private Temperature currentTemperature;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "day_min_temperature", nullable = false))
    private Temperature dayMinTemperature;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "day_max_temperature", nullable = false))
    private Temperature dayMaxTemperature;

    @Enumerated(EnumType.STRING)
    @Column(name = "sky_type", nullable = false)
    private SkyType skyType;

    @Enumerated(EnumType.STRING)
    @Column(name = "pty_type", nullable = false)
    private PtyType ptyType;

    // TODO: dto에서 생성 vs  vs ModelMapper 사용
    public static Weather from(WeatherDto weatherDto) {
        Temperature currentTemperature = Temperature.of(weatherDto.currentTemperature());
        Temperature dayMinTemperature = Temperature.of(weatherDto.dayMinTemperature());
        Temperature dayMaxTemperature = Temperature.of(weatherDto.dayMaxTemperature());
        validateCurrentAndDayTemperatures(currentTemperature, dayMinTemperature, dayMaxTemperature);
        SkyType skyType = SkyType.getByCode(weatherDto.skyCode());
        PtyType ptyType = PtyType.getByCode(weatherDto.ptyCode());

        return new Weather(currentTemperature, dayMinTemperature, dayMaxTemperature, skyType, ptyType);
    }

    private static void validateCurrentAndDayTemperatures(Temperature currentTemperature, Temperature dayMinTemperature,
                                                          Temperature dayMaxTemperature) {
        Assert.isTrue(dayMinTemperature.getValue() <= currentTemperature.getValue()
            && currentTemperature.getValue() <= dayMaxTemperature.getValue(), "현재 기온, 일 최저 기온, 일 최고 기온 값이 적절하지 않습니다.");
    }

}
