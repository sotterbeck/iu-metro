package de.sotterbeck.iumetro.infra.papermc.network;

import de.sotterbeck.iumetro.infra.papermc.auth.Role;
import de.sotterbeck.iumetro.infra.papermc.common.web.Routing;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.List;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

@Singleton
public class MetroNetworkRouting extends Routing<MetroNetworkController> {

    @Inject
    public MetroNetworkRouting() {
        super(MetroNetworkController.class);
    }

    @Override
    public void bindRoutes() {
        path("/metro-network", List.of(Role.AUTHENTICATED), () -> {
            get("/graph", ctx -> controller().getNetworkGraph(ctx));
        });
    }
}
