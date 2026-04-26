package de.sotterbeck.iumetro.app.auth;

import java.util.UUID;

public interface TokenRevocationService {

    void registerToken(UUID userId, String jti);

    boolean isRevoked(String jti);

    void revokeAllForUser(UUID userId);

}
