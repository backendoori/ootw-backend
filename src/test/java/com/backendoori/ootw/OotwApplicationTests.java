package com.backendoori.ootw;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.util.TimeZone;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OotwApplicationTests {

    @DisplayName("main 메서드를 실행할 수 있다")
    @Test
    void testMain() {
        // given
        String[] args = new String[0];

        // when
        ThrowingCallable main = () -> OotwApplication.main(args);

        // then
        assertThatNoException().isThrownBy(main);
    }

    @DisplayName("애플리케이션을 지정된 Tiemzone으로 설정한다")
    @Test
    void testServerTimezone() {
        // given // when
        String id = TimeZone.getDefault().getID();

        // then
        assertThat(id).isEqualTo(OotwApplication.TIMEZONE);
    }

}
