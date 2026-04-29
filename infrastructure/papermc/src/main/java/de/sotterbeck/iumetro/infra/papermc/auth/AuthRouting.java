package de.sotterbeck.iumetro.infra.papermc.auth;

import de.sotterbeck.iumetro.infra.papermc.common.web.Routing;
import io.javalin.Javalin;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class AuthRouting extends Routing<AuthController> {

    private final Javalin javalin;

    @Inject
    public AuthRouting(Javalin javalin) {
        super(AuthController.class);
        this.javalin = javalin;
    }

    @Override
    public void bindRoutes() {
        javalin.post("/api/auth/verify", ctx -> controller().verify(ctx), Role.ANYONE);
        javalin.post("/api/auth/refresh", ctx -> controller().refresh(ctx), Role.ANYONE);
        javalin.post("/api/auth/logout", ctx -> controller().logout(ctx), Role.ANYONE);
    }
}
