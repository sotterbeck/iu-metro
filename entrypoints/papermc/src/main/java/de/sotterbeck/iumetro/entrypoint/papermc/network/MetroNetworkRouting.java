package de.sotterbeck.iumetro.entrypoint.papermc.network;

import de.sotterbeck.iumetro.entrypoint.papermc.common.web.Routing;
import de.sotterbeck.iumetro.usecase.network.graph.ConnectionRequestModel;
import de.sotterbeck.iumetro.usecase.network.graph.MetroNetworkGraphInteractor;
import de.sotterbeck.iumetro.usecase.network.line.LineConnectionInteractor;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;

import java.util.UUID;

public class MetroNetworkRouting implements Routing {

    private final Javalin javalin;
    private final MetroNetworkGraphInteractor metroNetworkGraphInteractor;
    private final LineConnectionInteractor lineConnectionInteractor;

    public MetroNetworkRouting(Javalin javalin, MetroNetworkGraphInteractor metroNetworkGraphInteractor, LineConnectionInteractor lineConnectionInteractor) {
        this.javalin = javalin;
        this.metroNetworkGraphInteractor = metroNetworkGraphInteractor;
        this.lineConnectionInteractor = lineConnectionInteractor;
    }

    @Override
    public void bindRoutes() {
        javalin.get("/api/metro-network/graph", ctx -> ctx.json(metroNetworkGraphInteractor.getEntireNetwork()));

        javalin.post("/api/metro-network/connections", ctx -> {
            ConnectionRequestModel data = ctx.bodyAsClass(ConnectionRequestModel.class);
            lineConnectionInteractor.saveConnection(data);
            ctx.status(HttpStatus.CREATED);
        });

        javalin.put("/api/metro-network/connections/{station-1-id}/{station-2-id}", ctx -> {
            ConnectionRequestModel data = ctx.bodyAsClass(ConnectionRequestModel.class);
            UUID station1Id = UUID.fromString(ctx.pathParam("station-1-id"));
            UUID station2Id = UUID.fromString(ctx.pathParam("station-2-id"));

            ConnectionRequestModel request = new ConnectionRequestModel(station1Id, station2Id, data.lines());
            lineConnectionInteractor.saveConnection(request);
            ctx.status(HttpStatus.OK);
        }).exception(IllegalAccessException.class, (exception, ctx) -> ctx.status(HttpStatus.BAD_REQUEST));
    }

}
