package de.sotterbeck.iumetro.app.auth;

import de.sotterbeck.iumetro.domain.auth.Hashes;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public final class AuthService {

    private final AuthTokenRepository repository;
    private final TokenProvider tokenProvider;
    private final SecureTokenGenerator tokenGenerator;
    private final Clock clock;
    private final int refreshTokenTtlDays;

    public AuthService(AuthTokenRepository repository,
                       TokenProvider tokenProvider,
                       SecureTokenGenerator tokenGenerator,
                       int refreshTokenTtlDays, Clock clock) {
        this.repository = repository;
        this.tokenProvider = tokenProvider;
        this.tokenGenerator = tokenGenerator;
        this.clock = clock;
        this.refreshTokenTtlDays = refreshTokenTtlDays;
    }

    public VerifyResult verify(String token) {
        if (token == null || token.isBlank()) {
            return VerifyResult.invalid();
        }

        var tokenHash = Hashes.sha256Hex(token);

        Optional<MagicLinkTokenDto> foundToken = repository.findMagicLinkTokenByHash(tokenHash);
        if (foundToken.isEmpty()) {
            return VerifyResult.invalid();
        }

        var magicLinkToken = foundToken.get();
        if (magicLinkToken.expiresAt().isBefore(OffsetDateTime.now(clock))) {
            repository.deleteMagicLinkToken(tokenHash);
            return VerifyResult.expired();
        }

        repository.deleteMagicLinkToken(tokenHash);

        var userId = magicLinkToken.userId();
        var userName = magicLinkToken.userName();

        var accessToken = tokenProvider.generateAccessToken(userId, userName);
        var refreshToken = generateRefreshToken(userId, userName);

        return new VerifyResult.Success(accessToken, refreshToken, 900);
    }

    public RefreshResult refresh(String token) {
        if (token == null || token.isBlank()) {
            return RefreshResult.invalid();
        }

        var tokenHash = Hashes.sha256Hex(token);

        Optional<RefreshTokenDto> foundToken = repository.findRefreshTokenByHash(tokenHash);
        if (foundToken.isEmpty()) {
            return RefreshResult.invalid();
        }

        var refreshToken = foundToken.get();
        if (refreshToken.revokedAt() != null) {
            return RefreshResult.revoked();
        }

        if (refreshToken.expiresAt().isBefore(OffsetDateTime.now(clock))) {
            return RefreshResult.expired();
        }

        repository.revokeRefreshToken(tokenHash);

        var newRefreshToken = generateRefreshToken(refreshToken.userId(), refreshToken.userName());
        var accessToken = tokenProvider.generateAccessToken(refreshToken.userId(), refreshToken.userName());
        return new RefreshResult.Success(accessToken, newRefreshToken, 900);
    }

    public boolean logout(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }

        var tokenHash = Hashes.sha256Hex(token);

        repository.findRefreshTokenByHash(tokenHash)
                .ifPresent(refreshToken -> repository.revokeRefreshToken(tokenHash));

        return true;
    }

    private String generateRefreshToken(UUID userId, String userName) {
        var rawRefreshToken = tokenGenerator.generateSecureToken();
        var refreshHash = Hashes.sha256Hex(rawRefreshToken);
        var now = OffsetDateTime.now(clock);
        var refreshExpiresAt = now.plusDays(refreshTokenTtlDays);

        repository.saveRefreshToken(new RefreshTokenDto(
                null,
                userId,
                userName,
                refreshHash,
                refreshExpiresAt,
                null,
                now
        ));
        return rawRefreshToken;
    }

}
