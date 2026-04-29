package de.sotterbeck.iumetro.infra.papermc.network;

import de.sotterbeck.iumetro.app.network.line.LineService;
import de.sotterbeck.iumetro.infra.papermc.common.web.ApiResponse;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class LineController {

    private final LineService lineService;

    @Inject
    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    public void getAllLines(Context ctx) {
        ctx.json(ApiResponse.success(lineService.getAllLines()));
    }

    public void create(Context ctx) {
        LineService.LineRequestModel data = ctx.bodyAsClass(LineService.LineRequestModel.class);
        lineService.createLine(data);
        ctx.status(HttpStatus.CREATED);
        ctx.json(ApiResponse.success("Line created successfully"));
    }

    public void delete(Context ctx) {
        String name = ctx.pathParam("name");
        lineService.removeLine(name);
        ctx.status(HttpStatus.OK);
        ctx.json(ApiResponse.success("Line deleted successfully"));
    }

}
