package de.sotterbeck.iumetro.app.auth;

import de.sotterbeck.iumetro.domain.auth.Hashes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MagicLinkServiceTest {

    private static final String BASE_URL = "http://localhost:4556";
    private static final int MAGIC_LINK_TTL_MINUTES = 5;
    private static final UUID USER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final String USER_NAME = "TestPlayer";
    private static final Instant FIXED_INSTANT = Instant.parse("2025-01-01T00:00:00Z");
    private static final Clock FIXED_CLOCK = Clock.fixed(FIXED_INSTANT, ZoneOffset.UTC);

    @Mock
    private AuthTokenRepository authTokenRepository;

    @Mock
    private SecureTokenGenerator secureTokenGenerator;

    private MagicLinkService underTest;

    @BeforeEach
    void setUp() {
        underTest = new MagicLinkService(
                authTokenRepository,
                secureTokenGenerator,
                BASE_URL,
                MAGIC_LINK_TTL_MINUTES,
                FIXED_CLOCK
        );
    }

    @Test
    void generateLink_shouldGenerateSecureToken() {
        when(secureTokenGenerator.generateSecureToken()).thenReturn("some-ott-value");

        underTest.generateLink(USER_ID, USER_NAME);

        verify(secureTokenGenerator).generateSecureToken();
    }

    @Test
    void generateLink_shouldSaveHashedTokenToRepository() {
        String rawToken = "some-ott-value";
        when(secureTokenGenerator.generateSecureToken()).thenReturn(rawToken);

        underTest.generateLink(USER_ID, USER_NAME);

        ArgumentCaptor<MagicLinkTokenDto> captor = ArgumentCaptor.forClass(MagicLinkTokenDto.class);
        verify(authTokenRepository).saveMagicLinkToken(captor.capture());

        MagicLinkTokenDto saved = captor.getValue();
        assertThat(saved.tokenHash()).isEqualTo(Hashes.sha256Hex(rawToken));
        assertThat(saved.userId()).isEqualTo(USER_ID);
        assertThat(saved.userName()).isEqualTo(USER_NAME);
        assertThat(saved.createdAt()).isEqualTo(OffsetDateTime.ofInstant(FIXED_INSTANT, ZoneOffset.UTC));
        assertThat(saved.expiresAt()).isEqualTo(
                OffsetDateTime.ofInstant(FIXED_INSTANT.plus(Duration.ofMinutes(MAGIC_LINK_TTL_MINUTES)), ZoneOffset.UTC)
        );
    }

    @Test
    void generateLink_shouldReturnUrlWithToken() {
        String rawToken = "some-ott-value";
        when(secureTokenGenerator.generateSecureToken()).thenReturn(rawToken);

        MagicLinkResult result = underTest.generateLink(USER_ID, USER_NAME);

        assertThat(result.url()).isEqualTo(BASE_URL + "/api/auth/verify?token=" + rawToken);
    }

    @Test
    void generateLink_shouldReturnUrlWithDifferentToken() {
        String rawToken = "different-token-abc123";
        when(secureTokenGenerator.generateSecureToken()).thenReturn(rawToken);

        MagicLinkResult result = underTest.generateLink(USER_ID, USER_NAME);

        assertThat(result.url()).isEqualTo(BASE_URL + "/api/auth/verify?token=" + rawToken);
    }

    @Test
    void generateLink_shouldProduceUniqueUrlsForDifferentTokens() {
        when(secureTokenGenerator.generateSecureToken())
                .thenReturn("token-a")
                .thenReturn("token-b");

        MagicLinkResult first = underTest.generateLink(USER_ID, USER_NAME);
        MagicLinkResult second = underTest.generateLink(USER_ID, USER_NAME);

        assertThat(first.url()).isNotEqualTo(second.url());
    }

    @Test
    void generateLink_shouldSaveCorrectExpirationTimestamp() {
        when(secureTokenGenerator.generateSecureToken()).thenReturn("token");

        int customTtl = 10;
        MagicLinkService serviceWithCustomTtl = new MagicLinkService(
                authTokenRepository, secureTokenGenerator,
                BASE_URL,
                customTtl,
                FIXED_CLOCK
        );

        serviceWithCustomTtl.generateLink(USER_ID, USER_NAME);

        ArgumentCaptor<MagicLinkTokenDto> captor = ArgumentCaptor.forClass(MagicLinkTokenDto.class);
        verify(authTokenRepository).saveMagicLinkToken(captor.capture());

        assertThat(captor.getValue().expiresAt()).isEqualTo(
                OffsetDateTime.ofInstant(FIXED_INSTANT.plus(Duration.ofMinutes(customTtl)), ZoneOffset.UTC)
        );
    }

    @Test
    void generateLink_shouldHashTokenWithSha256() throws Exception {
        String rawToken = "test-token-for-hashing";
        when(secureTokenGenerator.generateSecureToken()).thenReturn(rawToken);

        underTest.generateLink(USER_ID, USER_NAME);

        ArgumentCaptor<MagicLinkTokenDto> captor = ArgumentCaptor.forClass(MagicLinkTokenDto.class);
        verify(authTokenRepository).saveMagicLinkToken(captor.capture());

        String expectedHash = Hashes.sha256Hex(rawToken);
        assertThat(captor.getValue().tokenHash()).isEqualTo(expectedHash);
        assertThat(captor.getValue().tokenHash()).hasSize(64);
    }

    @Test
    void generateLink_shouldNotStoreRawToken() {
        String rawToken = "raw-token-should-not-be-stored";
        when(secureTokenGenerator.generateSecureToken()).thenReturn(rawToken);

        underTest.generateLink(USER_ID, USER_NAME);

        ArgumentCaptor<MagicLinkTokenDto> captor = ArgumentCaptor.forClass(MagicLinkTokenDto.class);
        verify(authTokenRepository).saveMagicLinkToken(captor.capture());

        assertThat(captor.getValue().tokenHash()).isNotEqualTo(rawToken);
    }

}