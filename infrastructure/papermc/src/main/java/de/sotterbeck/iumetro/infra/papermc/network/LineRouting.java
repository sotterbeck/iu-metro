package de.sotterbeck.iumetro.infra.papermc.network;

import de.sotterbeck.iumetro.infra.papermc.auth.Role;
import de.sotterbeck.iumetro.infra.papermc.common.web.Routing;
import io.javalin.Javalin;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class LineRouting extends Routing<LineController> {

    private final Javalin javalin;

    @Inject
    public LineRouting(Javalin javalin) {
        super(LineController.class);
        this.javalin = javalin;
    }

    @Override
    public void bindRoutes() {
        javalin.get("/api/lines", ctx -> controller().getAllLines(ctx), Role.AUTHENTICATED);
        javalin.post("/api/lines", ctx -> controller().create(ctx), Role.AUTHENTICATED);
        javalin.delete("/api/lines/{name}", ctx -> controller().delete(ctx), Role.AUTHENTICATED);
    }
}
