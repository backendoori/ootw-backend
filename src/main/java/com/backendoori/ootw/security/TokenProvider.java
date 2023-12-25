package com.backendoori.ootw.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class TokenProvider {

    public boolean validateToken(String jwt) {
        return false;
    }

    public Authentication getAuthentication(String jwt) {
        return null;
    }

}
