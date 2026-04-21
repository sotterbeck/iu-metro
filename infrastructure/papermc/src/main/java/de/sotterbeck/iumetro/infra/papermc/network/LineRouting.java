package de.sotterbeck.iumetro.infra.papermc.network;

import de.sotterbeck.iumetro.app.network.line.LineService;
import de.sotterbeck.iumetro.infra.papermc.auth.Role;
import de.sotterbeck.iumetro.infra.papermc.common.web.ApiResponse;
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
        javalin.get("/api/lines", ctx -> ctx.json(ApiResponse.success(lineService.getAllLines())), Role.AUTHENTICATED);
        javalin.post("/api/lines", ctx -> {
            LineService.LineRequestModel data = ctx.bodyAsClass(LineService.LineRequestModel.class);
            lineService.createLine(data);
            ctx.status(HttpStatus.CREATED);
            ctx.json(ApiResponse.success("Line created successfully"));
        }, Role.AUTHENTICATED);
        javalin.delete("/api/lines/{name}", ctx -> {
            String name = ctx.pathParam("name");
            lineService.removeLine(name);
            ctx.status(HttpStatus.OK);
            ctx.json(ApiResponse.success("Line deleted successfully"));
        }, Role.AUTHENTICATED);

    }

}
