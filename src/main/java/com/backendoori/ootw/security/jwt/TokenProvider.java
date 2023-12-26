package com.backendoori.ootw.security.jwt;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class TokenProvider {

    public static final String USER_ID_CLAIM = "user_id";

    private final long tokenValidityInMilliseconds;
    private final String issuer;
    private final MacAlgorithm algorithm;
    private final SecretKey secretKey;
    private final JwtParser jwtParser;

    public TokenProvider(JwtProperties jwtProperties) {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.base64Secret());

        this.tokenValidityInMilliseconds = jwtProperties.tokenValidityInSeconds() * 1000;
        this.issuer = jwtProperties.issuer();
        this.algorithm = Jwts.SIG.HS512;
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.jwtParser = Jwts.parser()
            .verifyWith(secretKey)
            .build();
    }

    public String createToken(long userId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + tokenValidityInMilliseconds);

        return Jwts.builder()
            .issuer(issuer)
            .issuedAt(now)
            .expiration(validity)
            .claim(USER_ID_CLAIM, userId)
            .signWith(secretKey, algorithm)
            .compact();
    }

    public Authentication getAuthentication(String token) {
        long userId = jwtParser.parseSignedClaims(token)
            .getPayload()
            .get(USER_ID_CLAIM, Long.class);

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
