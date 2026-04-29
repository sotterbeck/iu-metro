package de.sotterbeck.iumetro.infra.papermc.network;

import de.sotterbeck.iumetro.infra.papermc.auth.Role;
import de.sotterbeck.iumetro.infra.papermc.common.web.Routing;
import io.javalin.Javalin;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class MetroNetworkRouting extends Routing<MetroNetworkController> {

    private final Javalin javalin;

    @Inject
    public MetroNetworkRouting(Javalin javalin) {
        super(MetroNetworkController.class);
        this.javalin = javalin;
    }

    @Override
    public void bindRoutes() {
        javalin.get("/api/metro-network/graph", ctx -> controller().getNetworkGraph(ctx), Role.AUTHENTICATED);
    }
}
