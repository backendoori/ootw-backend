package com.backendoori.ootw.security;

import com.backendoori.ootw.security.jwt.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class TokenMockMvcTest {

    protected String token;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected final void setToken(long userId) {
        token = tokenProvider.createToken(userId);
    }

}
