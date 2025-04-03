package de.sotterbeck.iumetro.infra.papermc.station.web;

import de.sotterbeck.iumetro.app.station.MetroStationManagingInteractor;
import de.sotterbeck.iumetro.infra.papermc.common.web.Routing;
import io.javalin.Javalin;

public class MetroStationRouting implements Routing {

    private final Javalin javalin;
    private final MetroStationManagingInteractor metroStationManagingInteractor;

    public MetroStationRouting(Javalin javalin, MetroStationManagingInteractor metroStationManagingInteractor) {
        this.javalin = javalin;
        this.metroStationManagingInteractor = metroStationManagingInteractor;
    }

    @Override
    public void bindRoutes() {
        System.out.println("MetroStationRouting.bindRoutes");
        javalin.get("/api/metro-stations", ctx ->
                ctx.json(metroStationManagingInteractor.getAll()));

        javalin.get("/api/metro-stations/positioned", ctx ->
                ctx.json(metroStationManagingInteractor.getAllPositioned())
        );
    }

}
