package de.sotterbeck.iumetro.infra.papermc.auth;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.multibindings.ProvidesIntoSet;
import de.sotterbeck.iumetro.app.auth.*;
import de.sotterbeck.iumetro.infra.papermc.common.CloudAnnotated;
import de.sotterbeck.iumetro.infra.papermc.common.IuMetroConfig;
import de.sotterbeck.iumetro.infra.papermc.common.web.Routing;
import de.sotterbeck.iumetro.infra.postgres.auth.PostgresAuthTokenRepository;

import java.time.Clock;
import java.time.Duration;

public class AuthModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AuthController.class);
        Multibinder.newSetBinder(binder(), Routing.class).addBinding().to(AuthRouting.class);
    }

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
                .audience("iu-metro-api")
                .clock(clock)
                .build();
    }

    @Provides
    @Singleton
    static TokenRevocationService provideTokenRevocationService(IuMetroConfig config, Clock clock) {
        return new InMemoryTokenRevocationService(
                Duration.ofMinutes(config.authAccessTokenTtlMinutes()),
                clock);
    }

    @Provides
    @Singleton
    static JwtAccessManager provideJwtAccessManager(TokenProvider tokenProvider, TokenRevocationService tokenRevocationService) {
        return new JwtAccessManager(tokenProvider, tokenRevocationService);
    }

    @Provides
    @Singleton
    static AuthService provideAuthService(AuthTokenRepository repository,
                                          TokenProvider tokenProvider,
                                          SecureTokenGenerator tokenGenerator,
                                          TokenRevocationService tokenRevocationService,
                                          IuMetroConfig config,
                                          Clock clock) {
        return new AuthService(
                repository,
                tokenProvider,
                tokenGenerator,
                tokenRevocationService,
                config.authRefreshTokenTtlDays(),
                (int) Duration.ofMinutes(config.authAccessTokenTtlMinutes()).getSeconds(),
                clock);
    }

    @Provides
    @Singleton
    static MagicLinkService provideMagicLinkService(AuthTokenRepository repository,
                                                    SecureTokenGenerator tokenGenerator,
                                                    IuMetroConfig config,
                                                    Clock clock) {
        return new MagicLinkService(repository, tokenGenerator, config.authBaseUrl(), config.authMagicLinkTtlMinutes(), clock);
    }

    @ProvidesIntoSet
    static CloudAnnotated provideLoginCommand(MagicLinkService magicLinkService) {
        return new LoginCommand(magicLinkService);
    }
}
