package com.backendoori.ootw.user.domain;


import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Builder
@RedisHash("certificate")
public class Certificate {

    @Id
    private String email;

    private String code;

}
