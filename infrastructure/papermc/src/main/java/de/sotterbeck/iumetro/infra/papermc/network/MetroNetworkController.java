package de.sotterbeck.iumetro.infra.papermc.network;

import de.sotterbeck.iumetro.app.network.graph.MetroNetworkGraphService;
import de.sotterbeck.iumetro.infra.papermc.common.web.ApiResponse;
import io.javalin.http.Context;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class MetroNetworkController {

    private final MetroNetworkGraphService metroNetworkGraphService;

    @Inject
    public MetroNetworkController(MetroNetworkGraphService metroNetworkGraphService) {
        this.metroNetworkGraphService = metroNetworkGraphService;
    }

    public void getNetworkGraph(Context ctx) {
        ctx.json(ApiResponse.success(metroNetworkGraphService.getEntireNetwork()));
    }

}
