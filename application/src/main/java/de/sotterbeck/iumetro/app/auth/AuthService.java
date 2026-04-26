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
    private final TokenRevocationService tokenRevocationService;
    private final Clock clock;
    private final int refreshTokenTtlDays;
    private final int accessTokenTtlSeconds;

    public AuthService(AuthTokenRepository repository,
                       TokenProvider tokenProvider,
                       SecureTokenGenerator tokenGenerator,
                       TokenRevocationService tokenRevocationService,
                       int refreshTokenTtlDays,
                       int accessTokenTtlSeconds,
                       Clock clock) {
        this.repository = repository;
        this.tokenProvider = tokenProvider;
        this.tokenGenerator = tokenGenerator;
        this.tokenRevocationService = tokenRevocationService;
        this.clock = clock;
        this.refreshTokenTtlDays = refreshTokenTtlDays;
        this.accessTokenTtlSeconds = accessTokenTtlSeconds;
    }

    public VerifyResult verify(String token) {
        if (token == null || token.isBlank()) {
            return VerifyResult.invalid();
        }

        var tokenHash = Hashes.sha256Hex(token);

        Optional<MagicLinkTokenDto> foundToken = repository.deleteMagicTokenByHash(tokenHash);
        if (foundToken.isEmpty()) {
            return VerifyResult.invalid();
        }

        var magicLinkToken = foundToken.get();
        if (magicLinkToken.expiresAt().isBefore(OffsetDateTime.now(clock))) {
            return VerifyResult.expired();
        }

        var userId = magicLinkToken.userId();
        var userName = magicLinkToken.userName();
        var role = magicLinkToken.role();

        var accessToken = tokenProvider.generateAccessToken(userId, userName, role);
        tokenRevocationService.registerToken(userId, accessToken.id());
        var refreshToken = generateRefreshToken(userId, userName, role);

        return new VerifyResult.Success(accessToken.token(), refreshToken, accessTokenTtlSeconds);
    }

    public RefreshResult refresh(String token) {
        if (token == null || token.isBlank()) {
            return RefreshResult.invalid();
        }

        var tokenHash = Hashes.sha256Hex(token);
        var newRawRefreshToken = tokenGenerator.generateSecureToken();
        var newRefreshHash = Hashes.sha256Hex(newRawRefreshToken);
        var now = OffsetDateTime.now(clock);
        var refreshExpiresAt = now.plusDays(refreshTokenTtlDays);


        // TODO: This is ugly. Fix before commit.
        var newRefreshToken = new RefreshTokenDto(
                null,
                null,
                null,
                null,
                newRefreshHash,
                refreshExpiresAt,
                null,
                now
        );

        var rotationResult = repository.rotateRefreshToken(tokenHash, newRefreshToken, now);

        return switch (rotationResult) {
            case TokenRotationDto.Success s -> {
                var accessToken = tokenProvider.generateAccessToken(s.userId(), s.userName(), s.role());
                tokenRevocationService.registerToken(s.userId(), accessToken.id());
                yield new RefreshResult.Success(accessToken.token(), newRawRefreshToken, accessTokenTtlSeconds);
            }
            case TokenRotationDto.Failure.NotFound ignored -> RefreshResult.invalid();
            case TokenRotationDto.Failure.Expired ignored -> RefreshResult.expired();
            case TokenRotationDto.Failure.Revoked ignored -> RefreshResult.revoked();
        };
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

    private String generateRefreshToken(UUID userId, String userName, String role) {
        var rawRefreshToken = tokenGenerator.generateSecureToken();
        var refreshHash = Hashes.sha256Hex(rawRefreshToken);
        var now = OffsetDateTime.now(clock);
        var refreshExpiresAt = now.plusDays(refreshTokenTtlDays);

        repository.saveRefreshToken(new RefreshTokenDto(
                null,
                userId,
                userName,
                role,
                refreshHash,
                refreshExpiresAt,
                null,
                now
        ));
        return rawRefreshToken;
    }

}
