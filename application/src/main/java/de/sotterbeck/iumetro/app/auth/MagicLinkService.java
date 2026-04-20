package de.sotterbeck.iumetro.app.auth;

import de.sotterbeck.iumetro.domain.auth.Hashes;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.UUID;

public final class MagicLinkService {

    private final SecureTokenGenerator tokenGenerator;
    private final AuthTokenRepository repository;
    private final String baseUrl;
    private final int magicLinkTtlMinutes;
    private final Clock clock;

    public MagicLinkService(SecureTokenGenerator tokenGenerator,
                            AuthTokenRepository repository,
                            String baseUrl,
                            int magicLinkTtlMinutes,
                            Clock clock) {
        this.tokenGenerator = tokenGenerator;
        this.repository = repository;
        this.baseUrl = baseUrl;
        this.magicLinkTtlMinutes = magicLinkTtlMinutes;
        this.clock = clock;
    }

    public MagicLinkResult generateLink(UUID userId, String userName) {
        var rawToken = tokenGenerator.generateSecureToken();
        var tokenHash = Hashes.sha256Hex(rawToken);
        var now = OffsetDateTime.now(clock);
        var expiresAt = now.plusMinutes(magicLinkTtlMinutes);

        repository.saveMagicLinkToken(new MagicLinkTokenDto(tokenHash, userId, userName, now, expiresAt));

        var url = "%s/api/auth/verify?token=%s"
                .formatted(baseUrl, rawToken);
        return new MagicLinkResult(url);
    }

}