package com.backendoori.ootw.user.domain;


import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Builder
@ToString
@RedisHash("certificate")
public class Certificate {

    @Id
    private String id;

    @Indexed
    private Long userId;

    private String code;

    @TimeToLive
    private Long expiration;

}
