package de.sotterbeck.iumetro.entrypoint.papermc.station.web;

import de.sotterbeck.iumetro.entrypoint.papermc.common.web.Routing;
import de.sotterbeck.iumetro.usecase.station.MetroStationManagingInteractor;
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
