package de.sotterbeck.iumetro.infra.papermc.station.web;

import de.sotterbeck.iumetro.app.station.MetroStationModificationService;
import de.sotterbeck.iumetro.app.station.MetroStationModificationService.Status;
import de.sotterbeck.iumetro.app.station.MetroStationService;
import de.sotterbeck.iumetro.infra.papermc.common.web.ApiResponse;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.List;

@Singleton
public class MetroStationController {

    private final MetroStationService metroStationService;
    private final MetroStationModificationService metroStationModificationService;

    @Inject
    public MetroStationController(MetroStationService metroStationService, MetroStationModificationService metroStationModificationService) {
        this.metroStationService = metroStationService;
        this.metroStationModificationService = metroStationModificationService;
    }

    public void getAll(Context ctx) {
        ctx.json(ApiResponse.success(metroStationService.getAll()));
    }

    public void getAllPositioned(Context ctx) {
        ctx.json(ApiResponse.success(metroStationService.getAllPositioned()));
    }

    public void saveLines(Context ctx) {
        String name = ctx.pathParam("name");
        var body = ctx.bodyAsClass(SaveLinesBody.class);

        var status = metroStationModificationService.saveLines(name, body.lines());

        if (status == Status.NOT_FOUND) {
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.json(ApiResponse.failure("Station not found"));
            return;
        }

        ctx.status(HttpStatus.OK);
        ctx.json(ApiResponse.success("Lines saved successfully"));
    }

    private record SaveLinesBody(List<String> lines) {

    }

}
