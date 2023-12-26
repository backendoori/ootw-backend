package com.backendoori.ootw.security.jwt;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class TokenProvider {

    private final long tokenValidityInMilliseconds;
    private final MacAlgorithm algorithm;
    private final SecretKey secretKey;
    private final JwtParser jwtParser;

    public TokenProvider() {
        this.tokenValidityInMilliseconds = 5 * 60 * 1000;
        this.algorithm = Jwts.SIG.HS512;
        this.secretKey = algorithm.key().build();
        this.jwtParser = Jwts.parser()
            .verifyWith(secretKey)
            .build();
    }

    public String createToken(long userId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + tokenValidityInMilliseconds);

        return Jwts.builder()
            .issuer("issuer")
            .issuedAt(now)
            .expiration(validity)
            .claim("user_id", userId)
            .signWith(secretKey, algorithm)
            .compact();
    }

    public Authentication getAuthentication(String token) {
        long userId = jwtParser.parseSignedClaims(token)
            .getPayload()
            .get("user_id", Long.class);

        return new UsernamePasswordAuthenticationToken(userId, token, Collections.emptyList());
    }

    public boolean validateToken(String token) {
        try {
            jwtParser.parseSignedClaims(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }

        return true;
    }

}
