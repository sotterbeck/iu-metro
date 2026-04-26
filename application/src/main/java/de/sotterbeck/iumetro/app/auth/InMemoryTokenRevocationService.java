package de.sotterbeck.iumetro.app.auth;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryTokenRevocationService implements TokenRevocationService {

    private final Map<UUID, Set<String>> userActiveJtis = new ConcurrentHashMap<>();
    private final Map<String, Instant> revokedJtis = new ConcurrentHashMap<>();
    private final Duration ttl;
    private final Clock clock;

    public InMemoryTokenRevocationService(Duration ttl, Clock clock) {
        this.ttl = ttl;
        this.clock = clock;
    }

    @Override
    public void registerToken(UUID userId, String jti) {
        userActiveJtis.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(jti);
    }

    @Override
    public boolean isRevoked(String jti) {
        var expiry = revokedJtis.get(jti);
        if (expiry == null) {
            return false;
        }
        if (clock.instant().isAfter(expiry)) {
            revokedJtis.remove(jti);
            return false;
        }
        return true;
    }

    @Override
    public void revokeAllForUser(UUID userId) {
        Set<String> jtis = userActiveJtis.remove(userId);
        if (jtis == null) {
            return;
        }

        var maxExpiry = clock.instant().plus(ttl);
        for (String jti : jtis) {
            revokedJtis.put(jti, maxExpiry);
        }
    }

}
