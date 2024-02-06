package com.backendoori.ootw.common;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class MailTest {

    protected static final String SMTP_ADDRESS = "greenmail@spring.io";
    private static final GreenMailConfiguration CONFIG = GreenMailConfiguration.aConfig()
        .withUser(SMTP_ADDRESS, "greenmail", "test");

    protected GreenMail smtp;

    @BeforeEach
    final void startSmtp() {
        smtp = new GreenMail(ServerSetupTest.SMTP)
            .withConfiguration(CONFIG);

        smtp.start();
    }

    @AfterEach
    final void stopSmtp() {
        smtp.stop();
    }

}
