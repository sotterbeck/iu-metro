package de.sotterbeck.iumetro.infra.papermc.auth;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.ProvidesIntoSet;
import de.sotterbeck.iumetro.app.auth.*;
import de.sotterbeck.iumetro.infra.papermc.common.CloudAnnotated;
import de.sotterbeck.iumetro.infra.papermc.common.IuMetroConfig;
import de.sotterbeck.iumetro.infra.papermc.common.web.Routing;
import de.sotterbeck.iumetro.infra.postgres.auth.PostgresAuthTokenRepository;
import io.javalin.Javalin;

import java.time.Clock;
import java.time.Duration;

public class AuthModule extends AbstractModule {

    @Provides
    @Singleton
    static Clock provideClock() {
        return Clock.systemUTC();
    }

    @Provides
    @Singleton
    static SecureTokenGenerator provideSecureTokenGenerator() {
        return new SecureTokenGenerator();
    }

    @Provides
    @Singleton
    static AuthTokenRepository provideAuthTokenRepository(javax.sql.DataSource dataSource) {
        return new PostgresAuthTokenRepository(dataSource);
    }

    @Provides
    @Singleton
    static TokenProvider provideTokenProvider(IuMetroConfig config, Clock clock) {
        return new JwtTokenProvider.Builder()
                .secret(config.authSecretKey())
                .expiration(Duration
                        .ofMinutes(config.authAccessTokenTtlMinutes())
                        .toMillis())
                .issuer("iu-metro")
                .clock(clock)
                .build();
    }

    @Provides
    @Singleton
    static JwtAccessManager provideJwtAccessManager(TokenProvider tokenProvider) {
        return new JwtAccessManager(tokenProvider);
    }

    @Provides
    @Singleton
    static MagicLinkService provideMagicLinkService(AuthTokenRepository repository,
                                                    SecureTokenGenerator tokenGenerator,
                                                    IuMetroConfig config,
                                                    Clock clock) {
        return new MagicLinkService(repository, tokenGenerator, config.authBaseUrl(), config.authMagicLinkTtlMinutes(), clock);
    }

    @Provides
    @Singleton
    static AuthService provideAuthService(AuthTokenRepository repository,
                                          TokenProvider tokenProvider,
                                          SecureTokenGenerator tokenGenerator,
                                          IuMetroConfig config,
                                          Clock clock) {
        return new AuthService(repository, tokenProvider, tokenGenerator, config.authRefreshTokenTtlDays(), clock);
    }

    @ProvidesIntoSet
    static Routing provideAuthRouting(Javalin javalin, AuthService authService) {
        return new AuthRouting(javalin, authService);
    }

    @ProvidesIntoSet
    static CloudAnnotated provideLoginCommand(MagicLinkService magicLinkService) {
        return new LoginCommand(magicLinkService);
    }

}
