package com.backendoori.ootw.user.domain;


import com.backendoori.ootw.common.AssertUtil;
import com.backendoori.ootw.user.validation.Message;
import com.backendoori.ootw.user.validation.RFC5322;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Builder
@RedisHash("certificate")
public class Certificate {

    public static final int SIZE = 6;
    public static final String REGEX = "^[a-zA-Z0-9]{" + SIZE + "}$";

    @Id
    private String id;

    private String code;

    public Certificate(String id, String code) {
        AssertUtil.hasPattern(id, RFC5322.REGEX, Message.INVALID_EMAIL);
        AssertUtil.hasPattern(code, REGEX, Message.INVALID_CERTIFICATE_CODE);

        this.id = id;
        this.code = code;
    }

}
