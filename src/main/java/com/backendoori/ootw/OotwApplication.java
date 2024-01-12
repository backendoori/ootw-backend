package com.backendoori.ootw;

import java.util.TimeZone;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class OotwApplication {

    public static final String TIMEZONE = "Asia/Seoul";

    public static void main(String[] args) {
        SpringApplication.run(OotwApplication.class, args);
    }

    @PostConstruct
    public void setTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone(TIMEZONE));
    }

}
