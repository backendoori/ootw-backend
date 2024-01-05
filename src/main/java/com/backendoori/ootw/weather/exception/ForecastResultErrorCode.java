package com.backendoori.ootw.weather.exception;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public enum ForecastResultErrorCode {
    APPLICATION_ERROR("01", IllegalAccessException::new),
    NODATA_ERROR("03", NoSuchElementException::new),
    INVALID_REQUEST_PARAMETER_ERROR("10", IllegalArgumentException::new);

    private static final String NORMAL_SERVICE_CODE = "00";
    private final String resultCode;
    private final Supplier<Exception> exceptionSupplier;

    private static void throwByResultCode(String resultCode) {
        Arrays.stream(values()).filter(code -> code.resultCode.equals(resultCode))
            .findFirst()
            .orElseThrow(IllegalStateException::new)
            .exceptionSupplier
            .get();
    }

    public static void checkNormalResultCode(String resultCode) {
        if (!resultCode.equals(NORMAL_SERVICE_CODE)) {
            throwByResultCode(resultCode);
        }
    }

}
