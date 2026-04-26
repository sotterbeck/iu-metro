package de.sotterbeck.iumetro.app.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryTokenRevocationServiceTest {

    private static final UUID USER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final String JTI_1 = "jti-1";
    private static final String JTI_2 = "jti-2";
    private static final Instant NOW = Instant.parse("2025-01-01T00:00:00Z");
    private static final Duration TTL = Duration.ofMinutes(15);

    private MutableClock mutableClock;
    private InMemoryTokenRevocationService underTest;

    @BeforeEach
    void setUp() {
        mutableClock = new MutableClock(NOW, ZoneOffset.UTC);
        underTest = new InMemoryTokenRevocationService(TTL, mutableClock);
    }

    @Test
    void isRevoked_shouldReturnFalse_whenJtiWasNeverRegistered() {
        assertThat(underTest.isRevoked(JTI_1)).isFalse();
    }

    @Test
    void isRevoked_shouldReturnFalse_whenJtiIsRegisteredButNotRevoked() {
        underTest.registerToken(USER_ID, JTI_1);

        assertThat(underTest.isRevoked(JTI_1)).isFalse();
    }

    @Test
    void isRevoked_shouldReturnTrue_whenJtiWasRevoked() {
        underTest.registerToken(USER_ID, JTI_1);
        underTest.revokeAllForUser(USER_ID);

        assertThat(underTest.isRevoked(JTI_1)).isTrue();
    }

    @Test
    void isRevoked_shouldCleanUpExpiredRevocation_whenCheckedAfterTtl() {
        underTest.registerToken(USER_ID, JTI_1);
        underTest.revokeAllForUser(USER_ID);

        assertThat(underTest.isRevoked(JTI_1)).isTrue();

        mutableClock.setInstant(NOW.plus(TTL).plusSeconds(1));

        assertThat(underTest.isRevoked(JTI_1)).isFalse();
    }

    @Test
    void revokeAllForUser_shouldOnlyRevokeTokensForThatUser() {
        UUID otherUserId = UUID.fromString("660e8400-e29b-41d4-a716-446655440000");
        underTest.registerToken(USER_ID, JTI_1);
        underTest.registerToken(otherUserId, JTI_2);

        underTest.revokeAllForUser(USER_ID);

        assertThat(underTest.isRevoked(JTI_1)).isTrue();
        assertThat(underTest.isRevoked(JTI_2)).isFalse();
    }

    @Test
    void revokeAllForUser_shouldRemoveAllActiveTokensForUser() {
        underTest.registerToken(USER_ID, JTI_1);
        underTest.registerToken(USER_ID, JTI_2);

        underTest.revokeAllForUser(USER_ID);

        assertThat(underTest.isRevoked(JTI_1)).isTrue();
        assertThat(underTest.isRevoked(JTI_2)).isTrue();
    }

    @Test
    void registerToken_shouldTrackMultipleTokensPerUser() {
        underTest.registerToken(USER_ID, JTI_1);
        underTest.registerToken(USER_ID, JTI_2);

        underTest.revokeAllForUser(USER_ID);

        assertThat(underTest.isRevoked(JTI_1)).isTrue();
        assertThat(underTest.isRevoked(JTI_2)).isTrue();
    }

    private static class MutableClock extends Clock {

        private Instant instant;
        private final ZoneId zone;

        MutableClock(Instant instant, ZoneId zone) {
            this.instant = instant;
            this.zone = zone;
        }

        void setInstant(Instant instant) {
            this.instant = instant;
        }

        @Override
        public ZoneId getZone() {
            return zone;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return new MutableClock(instant, zone);
        }

        @Override
        public Instant instant() {
            return instant;
        }

    }

}
