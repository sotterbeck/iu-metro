package de.sotterbeck.iumetro.infra.postgres.auth;

import de.sotterbeck.iumetro.app.auth.AuthTokenRepository;
import de.sotterbeck.iumetro.app.auth.MagicLinkTokenDto;
import de.sotterbeck.iumetro.app.auth.RefreshTokenDto;
import de.sotterbeck.iumetro.infra.postgres.jooq.generated.tables.records.MagicLinkTokensRecord;
import de.sotterbeck.iumetro.infra.postgres.jooq.generated.tables.records.RefreshTokensRecord;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.time.OffsetDateTime;
import java.util.Optional;

import static de.sotterbeck.iumetro.infra.postgres.jooq.generated.Tables.MAGIC_LINK_TOKENS;
import static de.sotterbeck.iumetro.infra.postgres.jooq.generated.Tables.REFRESH_TOKENS;

public class PostgresAuthTokenRepository implements AuthTokenRepository {

    private final DSLContext create;

    public PostgresAuthTokenRepository(DataSource dataSource) {
        this.create = DSL.using(dataSource, SQLDialect.POSTGRES);
    }

    @Override
    public void saveMagicLinkToken(MagicLinkTokenDto authToken) {
        MagicLinkTokensRecord record = create.newRecord(MAGIC_LINK_TOKENS)
                .setTokenHash(authToken.tokenHash())
                .setUserId(authToken.userId())
                .setUserName(authToken.userName())
                .setCreatedAt(authToken.createdAt())
                .setExpiresAt(authToken.expiresAt());
        record.store();
    }

    @Override
    public Optional<MagicLinkTokenDto> deleteMagicTokenByHash(String tokenHash) {
        return create.deleteFrom(MAGIC_LINK_TOKENS)
                .where(MAGIC_LINK_TOKENS.TOKEN_HASH.eq(tokenHash))
                .returning()
                .fetchOptional()
                .map(this::toMagicLinkTokenDto);
    }

    @Override
    public void saveRefreshToken(RefreshTokenDto refreshToken) {
        RefreshTokensRecord record = create.newRecord(REFRESH_TOKENS)
                .setId(refreshToken.id())
                .setUserId(refreshToken.userId())
                .setUserName(refreshToken.userName())
                .setTokenHash(refreshToken.tokenHash())
                .setExpiresAt(refreshToken.expiresAt())
                .setRevokedAt(refreshToken.revokedAt())
                .setCreatedAt(refreshToken.createdAt());
        record.store();
    }

    @Override
    public Optional<RefreshTokenDto> findRefreshTokenByHash(String tokenHash) {
        return create.fetchOptional(REFRESH_TOKENS, REFRESH_TOKENS.TOKEN_HASH.eq(tokenHash))
                .map(this::toRefreshTokenDto);
    }

    @Override
    public void revokeRefreshToken(String tokenHash) {
        create.update(REFRESH_TOKENS)
                .set(REFRESH_TOKENS.REVOKED_AT, OffsetDateTime.now())
                .where(REFRESH_TOKENS.TOKEN_HASH.eq(tokenHash))
                .execute();
    }

    @Override
    public void rotateRefreshToken(String oldTokenHash, RefreshTokenDto newToken, OffsetDateTime revokedAt) {
        create.transaction(configuration -> {
            DSLContext ctx = DSL.using(configuration);
            ctx.update(REFRESH_TOKENS)
                    .set(REFRESH_TOKENS.REVOKED_AT, revokedAt)
                    .where(REFRESH_TOKENS.TOKEN_HASH.eq(oldTokenHash))
                    .execute();

            RefreshTokensRecord record = ctx.newRecord(REFRESH_TOKENS)
                    .setId(newToken.id())
                    .setUserId(newToken.userId())
                    .setUserName(newToken.userName())
                    .setTokenHash(newToken.tokenHash())
                    .setExpiresAt(newToken.expiresAt())
                    .setRevokedAt(newToken.revokedAt())
                    .setCreatedAt(newToken.createdAt());
            record.store();
        });
    }

    private MagicLinkTokenDto toMagicLinkTokenDto(MagicLinkTokensRecord record) {
        return new MagicLinkTokenDto(
                record.getTokenHash(),
                record.getUserId(),
                record.getUserName(),
                record.getCreatedAt(),
                record.getExpiresAt()
        );
    }

    private RefreshTokenDto toRefreshTokenDto(RefreshTokensRecord record) {
        return new RefreshTokenDto(
                record.getId(),
                record.getUserId(),
                record.getUserName(),
                record.getTokenHash(),
                record.getExpiresAt(),
                record.getRevokedAt(),
                record.getCreatedAt()
        );
    }

}