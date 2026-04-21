package de.sotterbeck.iumetro.infra.papermc.auth;

import de.sotterbeck.iumetro.app.auth.AuthService;
import de.sotterbeck.iumetro.app.auth.RefreshResult;
import de.sotterbeck.iumetro.app.auth.VerifyResult;
import de.sotterbeck.iumetro.infra.papermc.common.web.ApiResponse;
import de.sotterbeck.iumetro.infra.papermc.common.web.Routing;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.util.Map;

public class AuthRouting implements Routing {

    private final Javalin javalin;
    private final AuthService authService;

    public AuthRouting(Javalin javalin, AuthService authService) {
        this.javalin = javalin;
        this.authService = authService;
    }

    @Override
    public void bindRoutes() {
        javalin.get("/api/auth/verify", ctx -> {
            String token = ctx.queryParam("token");
            if (token == null || token.isBlank()) {
                ctx.status(HttpStatus.BAD_REQUEST);
                ctx.json(ApiResponse.failure("Missing token parameter"));
                return;
            }

            var result = authService.verify(token);
            switch (result) {
                case VerifyResult.Success s -> ctx.json(Map.of(
                        "accessToken", s.accessToken(),
                        "refreshToken", s.refreshToken(),
                        "expiresIn", s.expiresIn()
                ));
                case VerifyResult.Expired ignored -> unauthorized(ctx);
                case VerifyResult.Invalid ignored -> unauthorized(ctx);
            }
        }, Role.ANYONE);

        javalin.post("/api/auth/refresh", ctx -> {
            var body = ctx.bodyAsClass(RefreshRequest.class);
            var result = authService.refresh(body.refreshToken());
            switch (result) {
                case RefreshResult.Success s -> ctx.json(Map.of(
                        "accessToken", s.accessToken(),
                        "expiresIn", s.expiresIn()
                ));
                case RefreshResult.Expired ignored -> unauthorized(ctx);
                case RefreshResult.Invalid ignored -> unauthorized(ctx);
                case RefreshResult.Revoked ignored -> unauthorized(ctx);
            }
        }, Role.ANYONE);

        javalin.post("/api/auth/logout", ctx -> {
            var body = ctx.bodyAsClass(LogoutRequest.class);
            authService.logout(body.refreshToken());
            ctx.json(ApiResponse.success("Logged out successfully"));
        }, Role.ANYONE);
    }

    private void unauthorized(Context ctx) {
        ctx.status(HttpStatus.UNAUTHORIZED);
        ctx.json(ApiResponse.failure("Invalid or expired token"));
    }

    private record RefreshRequest(String refreshToken) {

    }

    private record LogoutRequest(String refreshToken) {

    }

}
