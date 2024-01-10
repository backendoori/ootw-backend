package com.backendoori.ootw.common;

import java.util.Objects;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AssertUtil extends Assert {

    public static void notBlank(@Nullable String string, String message) {
        if (string == null || string.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void hasPattern(@Nullable String string, @Nullable String pattern, String message) {
        notNull(string, message);

        if (!string.matches(Objects.requireNonNull(pattern))) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void throwIf(boolean state, Supplier<RuntimeException> exceptionSupplier) {
        if (state) {
            throw exceptionSupplier.get();
        }
    }

    public static void isFalse(boolean state, String message) {
        if (!state) {
            throw new IllegalArgumentException(message);
        }
    }

}
