package de.sotterbeck.iumetro.app.auth;

import de.sotterbeck.iumetro.domain.auth.Hashes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final int REFRESH_TOKEN_TTL_DAYS = 7;
    private static final UUID USER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final String USER_NAME = "TestPlayer";
    private static final Instant FIXED_INSTANT = Instant.parse("2025-01-01T00:00:00Z");
    private static final Clock FIXED_CLOCK = Clock.fixed(FIXED_INSTANT, ZoneOffset.UTC);
    private static final String RAW_OTT = "valid-one-time-token";
    private static final String ACCESS_TOKEN = "eyJ.access.token";
    private static final String REFRESH_TOKEN = "dGZ.refresh.token";

    @Mock
    private AuthTokenRepository repository;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private SecureTokenGenerator tokenGenerator;

    private AuthService underTest;

    @BeforeEach
    void setUp() {
        underTest = new AuthService(repository, tokenProvider, tokenGenerator, REFRESH_TOKEN_TTL_DAYS, FIXED_CLOCK);
    }

    private static OffsetDateTime now() {
        return OffsetDateTime.ofInstant(FIXED_INSTANT, ZoneOffset.UTC);
    }

    private static OffsetDateTime minutesAgo(int minutes) {
        return now().minusMinutes(minutes);
    }

    private static OffsetDateTime minutesFromNow(int minutes) {
        return now().plusMinutes(minutes);
    }

    private static OffsetDateTime daysAgo(int days) {
        return now().minusDays(days);
    }

    private static OffsetDateTime daysFromNow(int days) {
        return now().plusDays(days);
    }

    @Nested
    class Verify {

        @Test
        void shouldReturnInvalid_forNullOrBlankToken() {
            assertThat(underTest.verify(null)).isInstanceOf(VerifyResult.Invalid.class);
            assertThat(underTest.verify("")).isInstanceOf(VerifyResult.Invalid.class);
        }

        @Test
        void shouldReturnInvalid_whenTokenNotFound() {
            String tokenHash = Hashes.sha256Hex(RAW_OTT);
            when(repository.findMagicLinkTokenByHash(tokenHash)).thenReturn(Optional.empty());

            VerifyResult result = underTest.verify(RAW_OTT);

            assertThat(result).isInstanceOf(VerifyResult.Invalid.class);
            verify(repository, never()).deleteMagicLinkToken(any());
            verify(tokenProvider, never()).generateAccessToken(any(), any());
            verify(tokenGenerator, never()).generateSecureToken();
        }

        @Test
        void shouldReturnExpired_whenTokenIsExpired() {
            String tokenHash = Hashes.sha256Hex(RAW_OTT);
            when(repository.findMagicLinkTokenByHash(tokenHash))
                    .thenReturn(Optional.of(magicLinkToken(tokenHash, minutesAgo(1))));

            VerifyResult result = underTest.verify(RAW_OTT);

            assertThat(result).isInstanceOf(VerifyResult.Expired.class);
            verify(repository).deleteMagicLinkToken(tokenHash);
            verify(tokenProvider, never()).generateAccessToken(any(), any());
            verify(tokenGenerator, never()).generateSecureToken();
        }

        @Test
        void shouldReturnExpired_whenTokenExpiresExactlyNow() {
            String tokenHash = Hashes.sha256Hex(RAW_OTT);
            when(repository.findMagicLinkTokenByHash(tokenHash))
                    .thenReturn(Optional.of(magicLinkToken(tokenHash, now().minusSeconds(1))));

            VerifyResult result = underTest.verify(RAW_OTT);

            assertThat(result).isInstanceOf(VerifyResult.Expired.class);
        }

        @Test
        void shouldHashTokenWithSha256_forLookup() {
            when(repository.findMagicLinkTokenByHash(any())).thenReturn(Optional.empty());

            underTest.verify(RAW_OTT);

            verify(repository).findMagicLinkTokenByHash(Hashes.sha256Hex(RAW_OTT));
        }

        @Test
        void shouldDeleteTokenAndGenerateTokensWithCorrectClaims_whenTokenIsValid() {
            String tokenHash = Hashes.sha256Hex(RAW_OTT);
            when(repository.findMagicLinkTokenByHash(tokenHash))
                    .thenReturn(Optional.of(magicLinkToken(tokenHash, minutesFromNow(4))));
            when(tokenProvider.generateAccessToken(USER_ID, USER_NAME)).thenReturn(ACCESS_TOKEN);
            when(tokenGenerator.generateSecureToken()).thenReturn(REFRESH_TOKEN);

            VerifyResult result = underTest.verify(RAW_OTT);

            assertThat(result).isInstanceOf(VerifyResult.Success.class);
            VerifyResult.Success success = (VerifyResult.Success) result;
            assertThat(success.accessToken()).isEqualTo(ACCESS_TOKEN);
            assertThat(success.refreshToken()).isEqualTo(REFRESH_TOKEN);
            assertThat(success.expiresIn()).isEqualTo(900);

            verify(repository).deleteMagicLinkToken(tokenHash);
            verify(tokenProvider).generateAccessToken(USER_ID, USER_NAME);
            verify(tokenGenerator).generateSecureToken();

            ArgumentCaptor<RefreshTokenDto> captor = ArgumentCaptor.forClass(RefreshTokenDto.class);
            verify(repository).saveRefreshToken(captor.capture());
            RefreshTokenDto saved = captor.getValue();
            assertThat(saved.tokenHash()).isEqualTo(Hashes.sha256Hex(REFRESH_TOKEN));
            assertThat(saved.userId()).isEqualTo(USER_ID);
            assertThat(saved.userName()).isEqualTo(USER_NAME);
            assertThat(saved.revokedAt()).isNull();
            assertThat(saved.expiresAt()).isEqualTo(daysFromNow(REFRESH_TOKEN_TTL_DAYS));
            assertThat(saved.createdAt()).isEqualTo(now());
            assertThat(saved.tokenHash()).isNotEqualTo(REFRESH_TOKEN);
            assertThat(saved.tokenHash()).hasSize(64);
        }

        @Test
        void shouldUseConfiguredRefreshTokenTtl() {
            String tokenHash = Hashes.sha256Hex(RAW_OTT);
            when(repository.findMagicLinkTokenByHash(tokenHash))
                    .thenReturn(Optional.of(magicLinkToken(tokenHash, minutesFromNow(4))));
            when(tokenProvider.generateAccessToken(any(), any())).thenReturn(ACCESS_TOKEN);
            when(tokenGenerator.generateSecureToken()).thenReturn(REFRESH_TOKEN);

            int customTtl = 14;
            AuthService customService = new AuthService(
                    repository, tokenProvider, tokenGenerator, customTtl, FIXED_CLOCK);

            customService.verify(RAW_OTT);

            ArgumentCaptor<RefreshTokenDto> captor = ArgumentCaptor.forClass(RefreshTokenDto.class);
            verify(repository).saveRefreshToken(captor.capture());
            assertThat(captor.getValue().expiresAt()).isEqualTo(daysFromNow(customTtl));
        }

        private MagicLinkTokenDto magicLinkToken(String tokenHash, OffsetDateTime expiresAt) {
            return new MagicLinkTokenDto(tokenHash, USER_ID, USER_NAME, now(), expiresAt);
        }

    }

    @Nested
    class Refresh {

        @Test
        void shouldReturnInvalid_forNullOrBlankToken() {
            assertThat(underTest.refresh(null)).isInstanceOf(RefreshResult.Invalid.class);
            assertThat(underTest.refresh("")).isInstanceOf(RefreshResult.Invalid.class);
            verify(repository, never()).findRefreshTokenByHash(any());
            verify(tokenProvider, never()).generateAccessToken(any(), any());
        }

        @Test
        void shouldReturnInvalid_whenTokenNotFound() {
            String tokenHash = Hashes.sha256Hex(REFRESH_TOKEN);
            when(repository.findRefreshTokenByHash(tokenHash)).thenReturn(Optional.empty());

            RefreshResult result = underTest.refresh(REFRESH_TOKEN);

            assertThat(result).isInstanceOf(RefreshResult.Invalid.class);
            verify(tokenProvider, never()).generateAccessToken(any(), any());
        }

        @Test
        void shouldReturnExpired_whenTokenIsExpired() {
            String tokenHash = Hashes.sha256Hex(REFRESH_TOKEN);
            when(repository.findRefreshTokenByHash(tokenHash))
                    .thenReturn(Optional.of(refreshToken(tokenHash, daysAgo(1), null)));

            RefreshResult result = underTest.refresh(REFRESH_TOKEN);

            assertThat(result).isInstanceOf(RefreshResult.Expired.class);
            verify(tokenProvider, never()).generateAccessToken(any(), any());
        }

        @Test
        void shouldReturnRevoked_whenTokenIsRevoked() {
            String tokenHash = Hashes.sha256Hex(REFRESH_TOKEN);
            when(repository.findRefreshTokenByHash(tokenHash))
                    .thenReturn(Optional.of(refreshToken(tokenHash, daysFromNow(6), minutesAgo(1))));

            RefreshResult result = underTest.refresh(REFRESH_TOKEN);

            assertThat(result).isInstanceOf(RefreshResult.Revoked.class);
            verify(tokenProvider, never()).generateAccessToken(any(), any());
        }

        @Test
        void shouldReturnNewAccessTokenWithCorrectClaims_whenTokenIsValid() {
            String tokenHash = Hashes.sha256Hex(REFRESH_TOKEN);
            when(repository.findRefreshTokenByHash(tokenHash))
                    .thenReturn(Optional.of(refreshToken(tokenHash, daysFromNow(6), null)));
            when(tokenProvider.generateAccessToken(USER_ID, USER_NAME)).thenReturn(ACCESS_TOKEN);

            RefreshResult result = underTest.refresh(REFRESH_TOKEN);

            assertThat(result).isInstanceOf(RefreshResult.Success.class);
            RefreshResult.Success success = (RefreshResult.Success) result;
            assertThat(success.accessToken()).isEqualTo(ACCESS_TOKEN);
            assertThat(success.expiresIn()).isEqualTo(900);
            verify(tokenProvider).generateAccessToken(USER_ID, USER_NAME);
        }

        @Test
        void shouldHashTokenWithSha256_forLookup() {
            when(repository.findRefreshTokenByHash(any())).thenReturn(Optional.empty());

            underTest.refresh(REFRESH_TOKEN);

            verify(repository).findRefreshTokenByHash(Hashes.sha256Hex(REFRESH_TOKEN));
        }

        private RefreshTokenDto refreshToken(String tokenHash, OffsetDateTime expiresAt, OffsetDateTime revokedAt) {
            return new RefreshTokenDto(UUID.randomUUID(), USER_ID, USER_NAME, tokenHash, expiresAt, revokedAt, daysAgo(1));
        }

    }

    @Nested
    class Logout {

        @Test
        void shouldReturnFalse_forNullOrBlankToken() {
            assertThat(underTest.logout(null)).isFalse();
            assertThat(underTest.logout("")).isFalse();
            verify(repository, never()).revokeRefreshToken(any());
        }

        @Test
        void shouldReturnTrue_whenTokenNotFound() {
            String tokenHash = Hashes.sha256Hex(REFRESH_TOKEN);
            when(repository.findRefreshTokenByHash(tokenHash)).thenReturn(Optional.empty());

            boolean result = underTest.logout(REFRESH_TOKEN);

            assertThat(result).isTrue();
            verify(repository, never()).revokeRefreshToken(any());
        }

        @Test
        void shouldRevokeTokenAndReturnTrue_whenTokenIsFound() {
            String tokenHash = Hashes.sha256Hex(REFRESH_TOKEN);
            when(repository.findRefreshTokenByHash(tokenHash))
                    .thenReturn(Optional.of(refreshToken(tokenHash, daysFromNow(6), null)));

            boolean result = underTest.logout(REFRESH_TOKEN);

            assertThat(result).isTrue();
            verify(repository).revokeRefreshToken(tokenHash);
        }

        @Test
        void shouldHashTokenWithSha256_forLookup() {
            when(repository.findRefreshTokenByHash(any())).thenReturn(Optional.empty());

            underTest.logout(REFRESH_TOKEN);

            verify(repository).findRefreshTokenByHash(Hashes.sha256Hex(REFRESH_TOKEN));
        }

        private RefreshTokenDto refreshToken(String tokenHash, OffsetDateTime expiresAt, OffsetDateTime revokedAt) {
            return new RefreshTokenDto(UUID.randomUUID(), USER_ID, USER_NAME, tokenHash, expiresAt, revokedAt, daysAgo(1));
        }

    }

}
