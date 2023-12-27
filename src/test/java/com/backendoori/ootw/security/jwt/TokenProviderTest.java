package com.backendoori.ootw.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import javax.crypto.SecretKey;
import java.util.Collection;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

class TokenProviderTest {

    static Faker faker = new Faker();

    String issuer;
    SecretKey key;
    String encodedKey;
    long tokenValidityInSeconds;
    TokenProvider tokenProvider;

    @BeforeEach
    void setup() {
        issuer = faker.name().firstName();
        key = generateSecretKey();
        encodedKey = encodeBytes(key.getEncoded());
        tokenValidityInSeconds = faker.number().numberBetween(10, Integer.MAX_VALUE);
        tokenProvider = createTokenProvider(issuer, encodedKey, tokenValidityInSeconds);
    }

    @DisplayName("사용자 id를 기반으로 토큰을 생성한다")
    @Test
    void testCreateToken() {
        // given
        long userId = faker.number().positive();

        // when
        String token = tokenProvider.createToken(userId);

        // then
        long payloadUserId = Jwts.parser()
            .verifyWith(key)
            .requireIssuer(issuer)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .get(TokenProvider.USER_ID_CLAIM, Long.class);

        assertThat(payloadUserId).isEqualTo(userId);
    }

    @DisplayName("토큰 기반으로 userId가 담긴 Authentication 인스턴스를 반환한다")
    @Test
    void testGetAuthentication() {
        // given
        long userId = faker.number().positive();

        String token = tokenProvider.createToken(userId);

        // when
        Authentication authentication = tokenProvider.getAuthentication(token);

        // then
        Object principal = authentication.getPrincipal();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        assertThat(principal).isExactlyInstanceOf(Long.class);
        assertThat((long) principal).isEqualTo(userId);
        assertThat(authorities).isEmpty();
    }

    @DisplayName("토근 검증 테스트")
    @Nested
    class validateTokenTest {

        @DisplayName("올바른 토큰은 true를 반환한다")
        @Test
        void success() {
            // given
            long userId = faker.number().positive();

            String token = tokenProvider.createToken(userId);

            // when
            boolean isValidToken = tokenProvider.validateToken(token);

            // then
            assertThat(isValidToken).isTrue();
        }

        @DisplayName("다른 issuer가 발급한 토큰은 false를 반환한다")
        @Test
        void failOtherIssuer() {
            // given
            String otherIssuer = faker.name().firstName();
            TokenProvider otherTokenProvider = createTokenProvider(otherIssuer, encodedKey, tokenValidityInSeconds);
            long userId = faker.number().positive();

            String otherToken = otherTokenProvider.createToken(userId);

            // when
            boolean isValidToken = tokenProvider.validateToken(otherToken);

            // then
            assertThat(isValidToken).isFalse();
        }

        @DisplayName("다른 키로 서명된 토큰은 false를 반환한다")
        @Test
        void failSignedOtherKey() {
            // given
            String otherKey = encodeBytes(generateSecretKey().getEncoded());
            TokenProvider otherTokenProvider = createTokenProvider(issuer, otherKey, tokenValidityInSeconds);
            long userId = faker.number().positive();

            String otherToken = otherTokenProvider.createToken(userId);

            // when
            boolean isValidToken = tokenProvider.validateToken(otherToken);

            // then
            assertThat(isValidToken).isFalse();
        }

        @DisplayName("기간이 만료된 토큰은 false를 반환한다")
        @Test
        void failExpiredJwt() {
            // given
            tokenProvider = createTokenProvider(issuer, encodedKey, 0);
            long userId = faker.number().positive();

            String token = tokenProvider.createToken(userId);

            // when
            boolean isValidToken = tokenProvider.validateToken(token);

            // then
            assertThat(isValidToken).isFalse();
        }

    }

    private SecretKey generateSecretKey() {
        return Jwts.SIG.HS512.key().build();
    }

    private String encodeBytes(byte[] bytes) {
        return Encoders.BASE64.encode(bytes);
    }

    private TokenProvider createTokenProvider(String issuer, String key, long tokenValidityInSeconds) {
        JwtProperties jwtProperties = new JwtProperties(issuer, key, tokenValidityInSeconds);

        return new TokenProvider(jwtProperties);
    }

}
