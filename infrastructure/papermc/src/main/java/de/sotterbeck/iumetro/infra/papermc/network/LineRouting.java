package de.sotterbeck.iumetro.infra.papermc.network;

import de.sotterbeck.iumetro.infra.papermc.auth.Role;
import de.sotterbeck.iumetro.infra.papermc.common.web.Routing;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.List;

import static io.javalin.apibuilder.ApiBuilder.*;

@Singleton
public class LineRouting extends Routing<LineController> {

    @Inject
    public LineRouting() {
        super(LineController.class);
    }

    @Override
    public void bindRoutes() {
        path("/lines", List.of(Role.AUTHENTICATED), () -> {
            get(ctx -> controller().getAllLines(ctx));
            post(ctx -> controller().create(ctx));
            delete("/{name}", ctx -> controller().delete(ctx));
        });
    }
}
