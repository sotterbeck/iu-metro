package de.sotterbeck.iumetro.infra.papermc.network;

import de.sotterbeck.iumetro.app.network.line.LineRequestModel;
import de.sotterbeck.iumetro.app.network.line.LineService;
import de.sotterbeck.iumetro.infra.papermc.common.web.Routing;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;

public class LineRouting implements Routing {

    private final Javalin javalin;
    private final LineService lineService;

    public LineRouting(Javalin javalin, LineService lineService) {
        this.javalin = javalin;
        this.lineService = lineService;
    }

    @Override
    public void bindRoutes() {
        javalin.get("/api/lines", ctx -> ctx.json(lineService.getAllLines()));
        javalin.post("api/lines", ctx -> {
            LineRequestModel data = ctx.bodyAsClass(LineRequestModel.class);
            lineService.createLine(data);
            ctx.status(HttpStatus.CREATED);
        });
        javalin.delete("/api/lines/{name}", ctx -> {
            String name = ctx.pathParam("name");
            lineService.removeLine(name);
            ctx.status(HttpStatus.OK);
        });

    }

}
