package de.sotterbeck.iumetro.infra.papermc.station.web;

import de.sotterbeck.iumetro.app.station.MetroStationService;
import de.sotterbeck.iumetro.infra.papermc.common.web.Routing;
import io.javalin.Javalin;

public class MetroStationRouting implements Routing {

    private final Javalin javalin;
    private final MetroStationService metroStationService;

    public MetroStationRouting(Javalin javalin, MetroStationService metroStationService) {
        this.javalin = javalin;
        this.metroStationService = metroStationService;
    }

    @Override
    public void bindRoutes() {
        System.out.println("MetroStationRouting.bindRoutes");
        javalin.get("/api/metro-stations", ctx ->
                ctx.json(metroStationService.getAll()));

        javalin.get("/api/metro-stations/positioned", ctx ->
                ctx.json(metroStationService.getAllPositioned())
        );
    }

}
