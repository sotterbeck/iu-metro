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
import java.util.UUID;

import static de.sotterbeck.iumetro.infra.postgres.jooq.generated.Tables.*;

public class PostgresAuthTokenRepository implements AuthTokenRepository {

    private final DSLContext create;

    public PostgresAuthTokenRepository(DataSource dataSource) {
        this.create = DSL.using(dataSource, SQLDialect.POSTGRES);
    }

    @Override
    public void saveMagicLinkToken(MagicLinkTokenDto authToken) {
        upsertUser(create, authToken.userId(), authToken.userName(), authToken.role(), authToken.createdAt());

        MagicLinkTokensRecord record = create.newRecord(MAGIC_LINK_TOKENS)
                .setTokenHash(authToken.tokenHash())
                .setUserId(authToken.userId())
                .setCreatedAt(authToken.createdAt())
                .setExpiresAt(authToken.expiresAt());
        record.store();
    }

    @Override
    public Optional<MagicLinkTokenDto> deleteMagicTokenByHash(String tokenHash) {
        return create.transactionResult(configuration -> {
            DSLContext ctx = DSL.using(configuration);

            MagicLinkTokenDto result = ctx.select(
                            MAGIC_LINK_TOKENS.TOKEN_HASH,
                            MAGIC_LINK_TOKENS.USER_ID,
                            USERS.NAME,
                            USERS.ROLE,
                            MAGIC_LINK_TOKENS.CREATED_AT,
                            MAGIC_LINK_TOKENS.EXPIRES_AT
                    )
                    .from(MAGIC_LINK_TOKENS)
                    .join(USERS).on(MAGIC_LINK_TOKENS.USER_ID.eq(USERS.ID))
                    .where(MAGIC_LINK_TOKENS.TOKEN_HASH.eq(tokenHash))
                    .fetchOne(r -> new MagicLinkTokenDto(
                            r.get(MAGIC_LINK_TOKENS.TOKEN_HASH),
                            r.get(MAGIC_LINK_TOKENS.USER_ID),
                            r.get(USERS.NAME),
                            r.get(USERS.ROLE),
                            r.get(MAGIC_LINK_TOKENS.CREATED_AT),
                            r.get(MAGIC_LINK_TOKENS.EXPIRES_AT)
                    ));

            if (result == null) {
                return Optional.empty();
            }

            ctx.deleteFrom(MAGIC_LINK_TOKENS)
                    .where(MAGIC_LINK_TOKENS.TOKEN_HASH.eq(tokenHash))
                    .execute();

            return Optional.of(result);
        });
    }

    @Override
    public void saveRefreshToken(RefreshTokenDto refreshToken) {
        upsertUser(create, refreshToken.userId(), refreshToken.userName(), refreshToken.role(), refreshToken.createdAt());

        RefreshTokensRecord record = create.newRecord(REFRESH_TOKENS)
                .setId(refreshToken.id())
                .setUserId(refreshToken.userId())
                .setTokenHash(refreshToken.tokenHash())
                .setExpiresAt(refreshToken.expiresAt())
                .setRevokedAt(refreshToken.revokedAt())
                .setCreatedAt(refreshToken.createdAt());
        record.store();
    }

    @Override
    public Optional<RefreshTokenDto> findRefreshTokenByHash(String tokenHash) {
        return Optional.ofNullable(create.select(
                        REFRESH_TOKENS.ID,
                        REFRESH_TOKENS.USER_ID,
                        USERS.NAME,
                        USERS.ROLE,
                        REFRESH_TOKENS.TOKEN_HASH,
                        REFRESH_TOKENS.EXPIRES_AT,
                        REFRESH_TOKENS.REVOKED_AT,
                        REFRESH_TOKENS.CREATED_AT
                )
                .from(REFRESH_TOKENS)
                .join(USERS).on(REFRESH_TOKENS.USER_ID.eq(USERS.ID))
                .where(REFRESH_TOKENS.TOKEN_HASH.eq(tokenHash))
                .fetchOne(r -> new RefreshTokenDto(
                        r.get(REFRESH_TOKENS.ID),
                        r.get(REFRESH_TOKENS.USER_ID),
                        r.get(USERS.NAME),
                        r.get(USERS.ROLE),
                        r.get(REFRESH_TOKENS.TOKEN_HASH),
                        r.get(REFRESH_TOKENS.EXPIRES_AT),
                        r.get(REFRESH_TOKENS.REVOKED_AT),
                        r.get(REFRESH_TOKENS.CREATED_AT)
                )));
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

            upsertUser(ctx, newToken.userId(), newToken.userName(), newToken.role(), newToken.createdAt());

            RefreshTokensRecord record = ctx.newRecord(REFRESH_TOKENS)
                    .setId(newToken.id())
                    .setUserId(newToken.userId())
                    .setTokenHash(newToken.tokenHash())
                    .setExpiresAt(newToken.expiresAt())
                    .setRevokedAt(newToken.revokedAt())
                    .setCreatedAt(newToken.createdAt());
            record.store();
        });
    }

    private void upsertUser(DSLContext ctx, UUID userId, String name, String role, OffsetDateTime timestamp) {
        ctx.insertInto(USERS)
                .set(USERS.ID, userId)
                .set(USERS.NAME, name)
                .set(USERS.ROLE, role)
                .set(USERS.CREATED_AT, timestamp)
                .set(USERS.UPDATED_AT, timestamp)
                .onConflict(USERS.ID)
                .doUpdate()
                .set(USERS.NAME, name)
                .set(USERS.ROLE, role)
                .set(USERS.UPDATED_AT, timestamp)
                .execute();
    }

}
