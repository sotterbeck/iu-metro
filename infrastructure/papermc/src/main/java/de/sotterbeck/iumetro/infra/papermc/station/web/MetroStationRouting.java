package de.sotterbeck.iumetro.infra.papermc.station.web;

import de.sotterbeck.iumetro.infra.papermc.auth.Role;
import de.sotterbeck.iumetro.infra.papermc.common.web.Routing;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.List;

import static io.javalin.apibuilder.ApiBuilder.*;

@Singleton
public class MetroStationRouting extends Routing<MetroStationController> {

    @Inject
    public MetroStationRouting() {
        super(MetroStationController.class);
    }

    @Override
    public void bindRoutes() {
        path("/metro-stations", List.of(Role.AUTHENTICATED), () -> {
            get(ctx -> controller().getAll(ctx));
            get("/positioned", ctx -> controller().getAllPositioned(ctx));
            put("/{name}/lines", ctx -> controller().saveLines(ctx));
        });
    }
}
