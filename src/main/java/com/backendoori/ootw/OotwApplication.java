package com.backendoori.ootw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class OotwApplication {

    public static void main(String[] args) {
        SpringApplication.run(OotwApplication.class, args);
    }

}
