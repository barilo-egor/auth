package tgb.cryptoexchange.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    private Key key;

    private String secret = "secret-secret-secret-secret-secret-secret-secret-";

    private long expiration = 3600000L;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(secret, expiration);
        key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("generateToken(String username) - валидный юзернейм - токен должен содержать юзернейм и валидное время жизни")
    void generateToken_ShouldContainCorrectUsernameAndExpiration() {
        String username = "testuser";
        String token = jwtService.generateToken(username);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertThat(claims.getSubject()).isEqualTo(username);
        assertThat(claims.getExpiration()).isAfter(new Date());
        assertThat(claims.getExpiration().getTime())
                .isCloseTo(System.currentTimeMillis() + expiration, Offset.offset(10000L));
    }

    @Test
    @DisplayName("generateToken(String username) - неверный ключ - проброс SecurityException")
    void generateToken_WithWrongKey_ShouldFailParsing() {
        String token = jwtService.generateToken("user");

        Key wrongKey = Keys.hmacShaKeyFor("another-secret-key-another-secret".getBytes(StandardCharsets.UTF_8));

        assertThrows(io.jsonwebtoken.security.SecurityException.class, () -> {
            Jwts.parserBuilder()
                    .setSigningKey(wrongKey)
                    .build()
                    .parseClaimsJws(token);
        });
    }
}