package de.sotterbeck.iumetro.entrypoint.papermc.network;

import de.sotterbeck.iumetro.entrypoint.papermc.common.web.Routing;
import de.sotterbeck.iumetro.usecase.network.line.LineManagingInteractor;
import de.sotterbeck.iumetro.usecase.network.line.LineRequestModel;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;

public class LineRouting implements Routing {

    private final Javalin javalin;
    private final LineManagingInteractor lineManagingInteractor;

    public LineRouting(Javalin javalin, LineManagingInteractor lineManagingInteractor) {
        this.javalin = javalin;
        this.lineManagingInteractor = lineManagingInteractor;
    }

    @Override
    public void bindRoutes() {
        javalin.get("/api/lines", ctx -> ctx.json(lineManagingInteractor.getAllLines()));
        javalin.post("api/lines", ctx -> {
            LineRequestModel data = ctx.bodyAsClass(LineRequestModel.class);
            lineManagingInteractor.createLine(data);
            ctx.status(HttpStatus.CREATED);
        });
        javalin.delete("/api/lines/{name}", ctx -> {
            String name = ctx.pathParam("name");
            lineManagingInteractor.removeLine(name);
            ctx.status(HttpStatus.OK);
        });

    }

}
