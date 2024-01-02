package com.backendoori.ootw.security;

import com.backendoori.ootw.security.jwt.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class TokenMockMvcTest {

    @Autowired
    private TokenProvider tokenProvider;

    protected String token;

    protected final void setToken(long userId) {
        token = tokenProvider.createToken(userId);
    }

}
