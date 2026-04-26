package de.sotterbeck.iumetro.infra.papermc.auth;

import de.sotterbeck.iumetro.app.auth.TokenProvider;
import de.sotterbeck.iumetro.app.auth.TokenRevocationService;
import de.sotterbeck.iumetro.app.auth.TokenValidationResult;
import de.sotterbeck.iumetro.infra.papermc.common.web.ApiResponse;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.security.RouteRole;

import java.util.Set;

public class JwtAccessManager {

    private final TokenProvider tokenProvider;
    private final TokenRevocationService tokenRevocationService;

    public JwtAccessManager(TokenProvider tokenProvider, TokenRevocationService tokenRevocationService) {
        this.tokenProvider = tokenProvider;
        this.tokenRevocationService = tokenRevocationService;
    }

    public void handle(Context ctx) {
        Set<RouteRole> routeRoles = ctx.routeRoles();

        if (routeRoles.isEmpty() || onlyAnyone(routeRoles)) {
            return;
        }

        String token = extractBearerToken(ctx);
        if (token == null) {
            deny(ctx, HttpStatus.UNAUTHORIZED, "Missing authorization header");
            return;
        }

        var result = tokenProvider.validate(token);
        if (result instanceof TokenValidationResult.Success s) {
            if (tokenRevocationService.isRevoked(s.jti())) {
                deny(ctx, HttpStatus.UNAUTHORIZED, "Invalid or expired token");
                return;
            }

            setUserAttributes(ctx, s);

            if (!hasMatchingRole(routeRoles, s.role())) {
                deny(ctx, HttpStatus.FORBIDDEN, "Forbidden");
            }

            return;
        }

        deny(ctx, HttpStatus.UNAUTHORIZED, "Invalid or expired token");
    }

    private boolean onlyAnyone(Set<RouteRole> routeRoles) {
        return routeRoles.stream().allMatch(r -> r == Role.ANYONE);
    }

    private String extractBearerToken(Context ctx) {
        String header = ctx.header("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private void setUserAttributes(Context ctx, TokenValidationResult.Success success) {
        ctx.attribute("userId", success.userId());
        ctx.attribute("userName", success.userName());
        ctx.attribute("role", success.role());
    }

    private boolean hasMatchingRole(Set<RouteRole> routeRoles, String userRole) {
        return routeRoles.stream()
                .filter(Role.class::isInstance)
                .map(Role.class::cast)
                .anyMatch(role -> role.permits(userRole));
    }

    private void deny(Context ctx, HttpStatus status, String message) {
        ctx.status(status);
        ctx.json(ApiResponse.failure(message));
        ctx.skipRemainingHandlers();
    }

}
