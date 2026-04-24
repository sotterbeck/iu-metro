package de.sotterbeck.iumetro.infra.papermc.auth;

import de.sotterbeck.iumetro.app.auth.AuthService;
import de.sotterbeck.iumetro.app.auth.RefreshResult;
import de.sotterbeck.iumetro.app.auth.VerifyResult;
import de.sotterbeck.iumetro.infra.papermc.common.web.ApiResponse;
import de.sotterbeck.iumetro.infra.papermc.common.web.Routing;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Cookie;
import io.javalin.http.HttpStatus;
import io.javalin.http.SameSite;

import java.time.Duration;
import java.util.Map;

public class AuthRouting implements Routing {

    private static final String REFRESH_TOKEN_COOKIE = "refresh_token";
    private static final String AUTH_PATH = "/api/auth";
    private static final int REFRESH_TOKEN_MAX_AGE_SECONDS = (int) Duration.ofDays(7).getSeconds();

    private final Javalin javalin;
    private final AuthService authService;

    public AuthRouting(Javalin javalin, AuthService authService) {
        this.javalin = javalin;
        this.authService = authService;
    }

    @Override
    public void bindRoutes() {
        javalin.get("/api/auth/verify", ctx -> {
            var token = ctx.queryParam("token");
            if (token == null || token.isBlank()) {
                ctx.status(HttpStatus.BAD_REQUEST);
                ctx.json(ApiResponse.failure("Missing token parameter"));
                return;
            }

            var result = authService.verify(token);
            switch (result) {
                case VerifyResult.Success s -> sendTokens(ctx, s.refreshToken(), s.accessToken(), s.expiresIn());
                case VerifyResult.Failure ignored -> unauthorized(ctx);
            }
        }, Role.ANYONE);

        javalin.post("/api/auth/refresh", ctx -> {
            var refreshToken = ctx.cookie(REFRESH_TOKEN_COOKIE);
            if (refreshToken == null || refreshToken.isBlank()) {
                unauthorized(ctx);
                return;
            }

            var result = authService.refresh(refreshToken);
            switch (result) {
                case RefreshResult.Success s -> sendTokens(ctx, s.refreshToken(), s.accessToken(), s.expiresIn());
                case RefreshResult.Failure ignored -> unauthorized(ctx);
            }
        }, Role.ANYONE);

        javalin.post("/api/auth/logout", ctx -> {
            var refreshToken = ctx.cookie(REFRESH_TOKEN_COOKIE);
            if (refreshToken != null && !refreshToken.isBlank()) {
                authService.logout(refreshToken);
            }
            clearRefreshTokenCookie(ctx);
            ctx.json(ApiResponse.success("Logged out successfully"));
        }, Role.ANYONE);
    }

    private void unauthorized(Context ctx) {
        ctx.status(HttpStatus.UNAUTHORIZED);
        ctx.json(ApiResponse.failure("Invalid or expired token"));
    }

    private void sendTokens(Context ctx, String refreshToken, String accessToken, long expiresIn) {
        setRefreshTokenCookie(ctx, refreshToken);
        ctx.json(Map.of(
                "accessToken", accessToken,
                "expiresIn", expiresIn
        ));
    }

    private void setRefreshTokenCookie(Context ctx, String refreshToken) {
        ctx.cookie(new Cookie(
                REFRESH_TOKEN_COOKIE,
                refreshToken,
                AUTH_PATH,
                REFRESH_TOKEN_MAX_AGE_SECONDS,
                true,
                0,
                true,
                null,
                null,
                SameSite.STRICT
        ));
    }

    /**
     * Clears the refresh token cookie by sending an empty, expired cookie with the same
     * security attributes as the original.
     * <p>
     * We do not use {@link Context#removeCookie(String, String)} because it does not set
     * {@code HttpOnly}, {@code Secure}, or {@code SameSite}. Modern browsers require these
     * attributes to match on the removal cookie for the original cookie to actually be deleted.
     *
     * @param ctx the context to clear the cookie from
     */
    private void clearRefreshTokenCookie(Context ctx) {
        ctx.cookie(new Cookie(
                REFRESH_TOKEN_COOKIE,
                "",
                AUTH_PATH,
                0,
                true,
                0,
                true,
                null,
                null,
                SameSite.STRICT
        ));
    }

}
