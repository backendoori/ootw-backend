package com.backendoori.ootw.weather.validation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Message {

    public static final String CAN_NOT_RETRIEVE_TEMPERATURE_ARRANGE = "기상청에서 제공한 일교차가 유효하지 않습니다.";
    public static final String CAN_NOT_RETRIEVE_SKYTYPE = "기상청 API에서 올바른 하늘 ㅐ상태 정보를 불러올 수 없습니다.";
    public static final String CAN_NOT_RETRIEVE_PTYTYPE = "기상청 API에서 올바른 강수 형태 정보를 불러올 수 없습니다.";
    public static final String CAN_NOT_USE_FORECAST_API = "기상청 API 서비스를 이용할 수 없습니다.";

    public static final String INVALID_LOCATION_MESSAGE = "nx, ny 좌표값 모두 null이 될 수 없고 0 이상 999 이하가 되어야 합니다.";

}
