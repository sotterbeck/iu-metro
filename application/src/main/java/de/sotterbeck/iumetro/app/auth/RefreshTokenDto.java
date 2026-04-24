package de.sotterbeck.iumetro.app.auth;

import java.time.OffsetDateTime;
import java.util.UUID;

public record RefreshTokenDto(
        UUID id,
        UUID userId,
        String userName,
        String role,
        String tokenHash,
        OffsetDateTime expiresAt,
        OffsetDateTime revokedAt,
        OffsetDateTime createdAt
) {

}
