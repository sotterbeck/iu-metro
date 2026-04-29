package de.sotterbeck.iumetro.infra.papermc.network;

import de.sotterbeck.iumetro.app.network.graph.MetroNetworkGraphService;
import de.sotterbeck.iumetro.infra.papermc.common.web.ApiResponse;
import de.sotterbeck.iumetro.infra.papermc.common.web.OpenApiSchema;
import io.javalin.http.Context;
import io.javalin.openapi.*;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class MetroNetworkController {

    private final MetroNetworkGraphService metroNetworkGraphService;

    @Inject
    public MetroNetworkController(MetroNetworkGraphService metroNetworkGraphService) {
        this.metroNetworkGraphService = metroNetworkGraphService;
    }

    @OpenApi(
            path = "/api/metro-network/graph",
            methods = HttpMethod.GET,
            summary = "Get metro network graph",
            operationId = "getNetworkGraph",
            tags = {"Metro Network"},
            description = "Returns the full metro network graph with stations as nodes and connections as links.",
            security = {@OpenApiSecurity(name = "bearerAuth")},
            responses = {
                    @OpenApiResponse(status = "200", description = "Network graph", content = @OpenApiContent(from = OpenApiSchema.NetworkGraphResponse.class))
            }
    )
    public void getNetworkGraph(Context ctx) {
        ctx.json(ApiResponse.success(metroNetworkGraphService.getEntireNetwork()));
    }

}
