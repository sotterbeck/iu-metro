package de.sotterbeck.iumetro.infra.papermc.auth;

import de.sotterbeck.iumetro.app.auth.TokenProvider;
import de.sotterbeck.iumetro.app.auth.TokenValidationResult;
import de.sotterbeck.iumetro.infra.papermc.common.web.ApiResponse;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.security.RouteRole;

import java.util.Set;

public class JwtAccessManager {

    private final TokenProvider tokenProvider;

    public JwtAccessManager(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    public void handle(Context ctx) throws Exception {
        Set<RouteRole> routeRoles = ctx.routeRoles();
        if (routeRoles.isEmpty() || routeRoles.contains(Role.ANYONE)) {
            return;
        }

        String authHeader = ctx.header("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            ctx.status(HttpStatus.UNAUTHORIZED);
            ctx.json(ApiResponse.failure("Missing authorization header"));
            ctx.skipRemainingHandlers();
            return;
        }

        String token = authHeader.substring(7);
        var validation = tokenProvider.validate(token);
        switch (validation) {
            case TokenValidationResult.Success s -> {
                ctx.attribute("userId", s.userId());
                ctx.attribute("userName", s.userName());
            }
            case TokenValidationResult.Expired e -> {
                ctx.status(HttpStatus.UNAUTHORIZED);
                ctx.json(ApiResponse.failure("Invalid or expired token"));
                ctx.skipRemainingHandlers();
                return;
            }
            case TokenValidationResult.Invalid i -> {
                ctx.status(HttpStatus.UNAUTHORIZED);
                ctx.json(ApiResponse.failure("Invalid or expired token"));
                ctx.skipRemainingHandlers();
                return;
            }
        }

        if (routeRoles.contains(Role.AUTHENTICATED)) {
            return;
        }

        ctx.status(HttpStatus.FORBIDDEN);
        ctx.json(ApiResponse.failure("Forbidden"));
        ctx.skipRemainingHandlers();
    }

}
