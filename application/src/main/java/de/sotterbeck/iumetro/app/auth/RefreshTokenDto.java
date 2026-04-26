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

    /**
     * Creates a new instance of {@code RefreshTokenDto} with the specified properties.
     *
     * @param userId    the unique identifier of the user
     * @param userName  the name of the user
     * @param role      the role of the user
     * @param tokenHash the hashed representation of the refresh token
     * @param expiresAt the expiration time of the refresh token
     * @param createdAt the creation time of the refresh token
     * @return a new {@code RefreshTokenDto} instance with the given information
     */
    public static RefreshTokenDto of(UUID userId, String userName, String role, String tokenHash,
                                     OffsetDateTime expiresAt, OffsetDateTime createdAt) {
        return new RefreshTokenDto(null, userId, userName, role, tokenHash, expiresAt, null, createdAt);
    }

    /**
     * Creates a new rotated {@code RefreshTokenDto} instance with the specified token hash, expiration time,
     * and creation time.
     *
     * @param tokenHash the hashed representation of the rotated refresh token
     * @param expiresAt the expiration time of the rotated refresh token
     * @param createdAt the creation time of the rotated refresh token
     * @return a new {@code RefreshTokenDto} instance with the provided details for a rotated refresh token
     */
    public static RefreshTokenDto ofRotated(String tokenHash, OffsetDateTime expiresAt, OffsetDateTime createdAt) {
        return new RefreshTokenDto(null, null, null, null, tokenHash, expiresAt, null, createdAt);
    }

}
