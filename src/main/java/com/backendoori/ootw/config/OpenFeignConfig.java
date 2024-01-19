package com.backendoori.ootw.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.backendoori.ootw.weather")
public class OpenFeignConfig {

}
