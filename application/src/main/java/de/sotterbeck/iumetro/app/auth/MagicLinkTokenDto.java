package de.sotterbeck.iumetro.app.auth;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MagicLinkTokenDto(
        String tokenHash,
        UUID userId,
        String userName,
        OffsetDateTime createdAt,
        OffsetDateTime expiresAt
) {

}