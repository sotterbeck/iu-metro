package de.sotterbeck.iumetro.infra.papermc.auth;

import de.sotterbeck.iumetro.infra.papermc.common.web.Routing;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.List;

import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;

@Singleton
public class AuthRouting extends Routing<AuthController> {

    @Inject
    public AuthRouting() {
        super(AuthController.class);
    }

    @Override
    public void bindRoutes() {
        path("/auth", List.of(Role.ANYONE), () -> {
            post("/verify", ctx -> controller().verify(ctx));
            post("/refresh", ctx -> controller().refresh(ctx));
            post("/logout", ctx -> controller().logout(ctx));
        });
    }
}
