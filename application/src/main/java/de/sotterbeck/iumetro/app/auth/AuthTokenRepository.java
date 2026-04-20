package de.sotterbeck.iumetro.app.auth;

import java.util.Optional;
import java.util.UUID;

public interface AuthTokenRepository {

    void saveMagicLinkToken(MagicLinkTokenDto authToken);

    Optional<MagicLinkTokenDto> findMagicLinkTokenByHash(String tokenHash);

    void deleteMagicLinkToken(String tokenHash);

    void saveRefreshToken(RefreshTokenDto refreshToken);

    Optional<RefreshTokenDto> findRefreshTokenByHash(String tokenHash);

    void revokeRefreshToken(String tokenHash);

    void revokeAllRefreshTokensForUser(UUID userUuid);

}