package de.sotterbeck.iumetro.infra.papermc.network;

import de.sotterbeck.iumetro.app.network.graph.MetroNetworkGraphService;
import de.sotterbeck.iumetro.infra.papermc.common.web.Routing;
import io.javalin.Javalin;

public class MetroNetworkRouting implements Routing {

    private final Javalin javalin;
    private final MetroNetworkGraphService metroNetworkGraphService;

    public MetroNetworkRouting(Javalin javalin, MetroNetworkGraphService metroNetworkGraphService) {
        this.javalin = javalin;
        this.metroNetworkGraphService = metroNetworkGraphService;
    }

    @Override
    public void bindRoutes() {
        javalin.get("/api/metro-network/graph", ctx -> ctx.json(metroNetworkGraphService.getEntireNetwork()));
    }

}
