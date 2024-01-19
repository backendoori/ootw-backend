package com.backendoori.ootw.weather.exception;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public enum ForecastResultErrorManager {

    APPLICATION_ERROR("01", IllegalArgumentException::new),
    NODATA_ERROR("03", NoSuchElementException::new),
    INVALID_REQUEST_PARAMETER_ERROR("10", IllegalArgumentException::new);

    private static final String API_SERVER_ERROR_MESSAGE = "기상청 API 서비스를 이용할 수 없습니다.";
    private static final String NORMAL_SERVICE_CODE = "00";

    private final String resultCode;
    private final Function<String, RuntimeException> exceptionThrower;

    private static void throwByErrorCode(String resultCode) {
        Arrays.stream(values())
            .filter(code -> Objects.equals(resultCode, code.resultCode))
            .findFirst()
            .orElseThrow(ForecastResultErrorManager::getApiServerException)
            .throwException();
    }

    public static void checkResultCode(String resultCode) {
        if (!Objects.equals(resultCode, NORMAL_SERVICE_CODE)) {
            throwByErrorCode(resultCode);
        }
    }

    public static IllegalStateException getApiServerException() {
        return new IllegalStateException(API_SERVER_ERROR_MESSAGE);
    }

    private void throwException() {
        throw this.exceptionThrower.apply(this.name());
    }

}
