package de.sotterbeck.iumetro.infra.papermc.station.web;

import de.sotterbeck.iumetro.app.station.MetroStationModificationService;
import de.sotterbeck.iumetro.app.station.MetroStationModificationService.Status;
import de.sotterbeck.iumetro.app.station.MetroStationService;
import de.sotterbeck.iumetro.infra.papermc.common.web.Routing;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;

import java.util.List;

public class MetroStationRouting implements Routing {

    private final Javalin javalin;
    private final MetroStationService metroStationService;
    private final MetroStationModificationService metroStationModificationService;

    public MetroStationRouting(Javalin javalin, MetroStationService metroStationService, MetroStationModificationService metroStationModificationService) {
        this.javalin = javalin;
        this.metroStationService = metroStationService;
        this.metroStationModificationService = metroStationModificationService;
    }

    @Override
    public void bindRoutes() {
        javalin.get("/api/metro-stations", ctx ->
                ctx.json(metroStationService.getAll())
        );

        javalin.get("/api/metro-stations/positioned", ctx ->
                ctx.json(metroStationService.getAllPositioned())
        );

        javalin.put("/api/metro-stations/{name}/lines", ctx -> {
            String name = ctx.pathParam("name");
            var body = ctx.bodyAsClass(SaveLinesBody.class);

            var status = metroStationModificationService.saveLines(name, body.lines());

            if (status == Status.NOT_FOUND) {
                ctx.status(HttpStatus.NOT_FOUND);
                return;
            }

            ctx.status(HttpStatus.OK);
        });

    }

    private record SaveLinesBody(List<String> lines) {

    }


}
