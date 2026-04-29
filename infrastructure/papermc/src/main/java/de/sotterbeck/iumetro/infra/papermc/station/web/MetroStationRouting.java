package de.sotterbeck.iumetro.infra.papermc.station.web;

import de.sotterbeck.iumetro.infra.papermc.auth.Role;
import de.sotterbeck.iumetro.infra.papermc.common.web.Routing;
import io.javalin.Javalin;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class MetroStationRouting extends Routing<MetroStationController> {

    private final Javalin javalin;

    @Inject
    public MetroStationRouting(Javalin javalin) {
        super(MetroStationController.class);
        this.javalin = javalin;
    }

    @Override
    public void bindRoutes() {
        javalin.get("/api/metro-stations", ctx -> controller().getAll(ctx), Role.AUTHENTICATED);
        javalin.get("/api/metro-stations/positioned", ctx -> controller().getAllPositioned(ctx), Role.AUTHENTICATED);
        javalin.put("/api/metro-stations/{name}/lines", ctx -> controller().saveLines(ctx), Role.AUTHENTICATED);
    }
}
