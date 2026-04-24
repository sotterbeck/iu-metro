package de.sotterbeck.iumetro.app.auth;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface AuthTokenRepository {

    void saveMagicLinkToken(MagicLinkTokenDto authToken);

    Optional<MagicLinkTokenDto> deleteMagicTokenByHash(String tokenHash);

    void saveRefreshToken(RefreshTokenDto refreshToken);

    Optional<RefreshTokenDto> findRefreshTokenByHash(String tokenHash);

    void revokeRefreshToken(String tokenHash);

    void rotateRefreshToken(String oldTokenHash, RefreshTokenDto newToken, OffsetDateTime revokedAt);

}