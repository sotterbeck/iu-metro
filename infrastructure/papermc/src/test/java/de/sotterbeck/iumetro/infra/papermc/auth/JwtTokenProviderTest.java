package de.sotterbeck.iumetro.infra.papermc.auth;

import de.sotterbeck.iumetro.app.auth.TokenValidationResult;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private static final String SECRET = "this-is-a-test-secret-at-least-32-chars";
    private static final String ISSUER = "iumetro-test";
    private static final long EXPIRATION_MS = 900_000L;
    private static final UUID USER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final String USER_NAME = "TestPlayer";

    private JwtTokenProvider providerWithClock(Clock clock) {
        return new JwtTokenProvider.Builder()
                .secret(SECRET)
                .issuer(ISSUER)
                .expiration(EXPIRATION_MS)
                .clock(clock)
                .build();
    }

    @Test
    void validate_ShouldReturnSuccessWithUserIdAndUserName_WhenTokenIsValid() {
        Clock clock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC);
        JwtTokenProvider provider = providerWithClock(clock);
        String token = provider.generateAccessToken(USER_ID, USER_NAME, "player").token();

        TokenValidationResult result = provider.validate(token);

        assertThat(result).isInstanceOf(TokenValidationResult.Success.class);
        TokenValidationResult.Success success = (TokenValidationResult.Success) result;
        assertThat(success.userId()).isEqualTo(USER_ID);
        assertThat(success.userName()).isEqualTo(USER_NAME);
    }

    @Test
    void validate_ShouldReturnExpired_WhenTokenIsExpired() {
        Instant issuedAt = Instant.parse("2025-01-01T00:00:00Z");
        JwtTokenProvider issuanceProvider = providerWithClock(
                Clock.fixed(issuedAt, ZoneOffset.UTC));

        String token = issuanceProvider.generateAccessToken(USER_ID, USER_NAME, "player").token();

        JwtTokenProvider validationProvider = providerWithClock(
                Clock.fixed(issuedAt.plusMillis(EXPIRATION_MS + 1), ZoneOffset.UTC));

        TokenValidationResult result = validationProvider.validate(token);

        assertThat(result).isInstanceOf(TokenValidationResult.Expired.class);
    }

    @Test
    void validate_ShouldReturnSuccess_WhenTokenIsJustBeforeExpiry() {
        Instant issuedAt = Instant.parse("2025-01-01T00:00:00Z");
        JwtTokenProvider issuanceProvider = providerWithClock(
                Clock.fixed(issuedAt, ZoneOffset.UTC));

        String token = issuanceProvider.generateAccessToken(USER_ID, USER_NAME, "player").token();

        JwtTokenProvider validationProvider = providerWithClock(
                Clock.fixed(issuedAt.plusMillis(EXPIRATION_MS - 1), ZoneOffset.UTC));

        TokenValidationResult result = validationProvider.validate(token);

        assertThat(result).isInstanceOf(TokenValidationResult.Success.class);
    }

    @Test
    void validate_ShouldReturnInvalid_WhenTokenSignedWithDifferentSecret() {
        Clock clock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC);
        JwtTokenProvider provider = providerWithClock(clock);
        String token = provider.generateAccessToken(USER_ID, USER_NAME, "player").token();

        JwtTokenProvider otherProvider = new JwtTokenProvider.Builder()
                .secret("a-completely-different-secret-key-32c")
                .issuer(ISSUER)
                .expiration(EXPIRATION_MS)
                .clock(clock)
                .build();

        TokenValidationResult result = otherProvider.validate(token);

        assertThat(result).isInstanceOf(TokenValidationResult.Invalid.class);
    }

    @Test
    void validate_ShouldReturnInvalid_WhenTokenIssuerDoesNotMatch() {
        Clock clock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC);
        JwtTokenProvider provider = providerWithClock(clock);
        String token = provider.generateAccessToken(USER_ID, USER_NAME, "player").token();

        JwtTokenProvider otherProvider = new JwtTokenProvider.Builder()
                .secret(SECRET)
                .issuer("wrong-issuer")
                .expiration(EXPIRATION_MS)
                .clock(clock)
                .build();

        TokenValidationResult result = otherProvider.validate(token);

        assertThat(result).isInstanceOf(TokenValidationResult.Invalid.class);
    }

    @Test
    void validate_ShouldReturnInvalid_WhenTokenAudienceDoesNotMatch() {
        Clock clock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC);
        JwtTokenProvider provider = new JwtTokenProvider.Builder()
                .secret(SECRET)
                .issuer(ISSUER)
                .audience("iu-metro-api")
                .expiration(EXPIRATION_MS)
                .clock(clock)
                .build();
        String token = provider.generateAccessToken(USER_ID, USER_NAME, "player").token();

        JwtTokenProvider otherProvider = new JwtTokenProvider.Builder()
                .secret(SECRET)
                .issuer(ISSUER)
                .audience("wrong-audience")
                .expiration(EXPIRATION_MS)
                .clock(clock)
                .build();

        TokenValidationResult result = otherProvider.validate(token);

        assertThat(result).isInstanceOf(TokenValidationResult.Invalid.class);
    }

    @Test
    void validate_ShouldReturnInvalid_WhenTokenIsMalformed() {
        JwtTokenProvider provider = providerWithClock(
                Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC));

        TokenValidationResult result = provider.validate("not.a.valid.jwt");

        assertThat(result).isInstanceOf(TokenValidationResult.Invalid.class);
    }

    @Test
    void validate_ShouldReturnInvalid_WhenTokenIsGarbledInput() {
        JwtTokenProvider provider = providerWithClock(
                Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC));

        TokenValidationResult result = provider.validate("$$garbage$$!");

        assertThat(result).isInstanceOf(TokenValidationResult.Invalid.class);
    }

    @Test
    void validate_ShouldReturnInvalid_WhenTokenIsEmptyString() {
        JwtTokenProvider provider = providerWithClock(
                Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC));

        TokenValidationResult result = provider.validate("");

        assertThat(result).isInstanceOf(TokenValidationResult.Invalid.class);
    }

    @Test
    void validate_ShouldReturnInvalid_WhenTokenIsNull() {
        JwtTokenProvider provider = providerWithClock(
                Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC));

        TokenValidationResult result = provider.validate(null);

        assertThat(result).isInstanceOf(TokenValidationResult.Invalid.class);
    }

    @Test
    void generateAccessToken_ShouldPreserveUserId_WhenValidated() {
        Clock clock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC);
        JwtTokenProvider provider = providerWithClock(clock);
        UUID userId = UUID.randomUUID();

        String token = provider.generateAccessToken(userId, "player1", "player").token();
        TokenValidationResult.Success result = (TokenValidationResult.Success) provider.validate(token);

        assertThat(result.userId()).isEqualTo(userId);
    }

    @Test
    void generateAccessToken_ShouldPreserveUserName_WhenValidated() {
        Clock clock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC);
        JwtTokenProvider provider = providerWithClock(clock);
        String userName = "AnotherPlayer";

        String token = provider.generateAccessToken(USER_ID, userName, "player").token();
        TokenValidationResult.Success result = (TokenValidationResult.Success) provider.validate(token);

        assertThat(result.userName()).isEqualTo(userName);
    }

    @Test
    void generateAccessToken_ShouldIncludeRoleClaim_WhenValidated() {
        Clock clock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC);
        JwtTokenProvider provider = providerWithClock(clock);

        String token = provider.generateAccessToken(USER_ID, USER_NAME, "player").token();
        TokenValidationResult.Success result = (TokenValidationResult.Success) provider.validate(token);

        assertThat(result.role()).isEqualTo("player");
    }

    @Test
    void generateAccessToken_ShouldIncludeJtiClaim_WhenValidated() {
        Clock clock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC);
        JwtTokenProvider provider = providerWithClock(clock);

        String token = provider.generateAccessToken(USER_ID, USER_NAME, "player").token();
        TokenValidationResult.Success result = (TokenValidationResult.Success) provider.validate(token);

        assertThat(result.jti()).isNotNull();
        assertThat(result.jti()).isNotBlank();
    }

    @Test
    void generateAccessToken_ShouldReturnDistinctJti_ForEachToken() {
        Clock clock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC);
        JwtTokenProvider provider = providerWithClock(clock);

        String token1 = provider.generateAccessToken(USER_ID, USER_NAME, "player").token();
        String token2 = provider.generateAccessToken(USER_ID, USER_NAME, "player").token();

        TokenValidationResult.Success result1 = (TokenValidationResult.Success) provider.validate(token1);
        TokenValidationResult.Success result2 = (TokenValidationResult.Success) provider.validate(token2);

        assertThat(result1.jti()).isNotEqualTo(result2.jti());
    }

    @Test
    void validate_ShouldYieldDistinctIdentities_WhenTokensAreForDifferentUsers() {
        Clock clock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC);
        JwtTokenProvider provider = providerWithClock(clock);
        UUID otherUserId = UUID.fromString("660e8400-e29b-41d4-a716-446655440000");

        String token1 = provider.generateAccessToken(USER_ID, "player1", "player").token();
        String token2 = provider.generateAccessToken(otherUserId, "player2", "player").token();

        TokenValidationResult.Success result1 = (TokenValidationResult.Success) provider.validate(token1);
        TokenValidationResult.Success result2 = (TokenValidationResult.Success) provider.validate(token2);

        assertThat(result1.userId()).isNotEqualTo(result2.userId());
        assertThat(result1.userName()).isNotEqualTo(result2.userName());
    }

}