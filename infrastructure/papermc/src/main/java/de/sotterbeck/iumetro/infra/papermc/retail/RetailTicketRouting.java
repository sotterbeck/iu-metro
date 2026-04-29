package de.sotterbeck.iumetro.infra.papermc.retail;

import de.sotterbeck.iumetro.infra.papermc.auth.Role;
import de.sotterbeck.iumetro.infra.papermc.common.web.Routing;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.List;

import static io.javalin.apibuilder.ApiBuilder.*;

@Singleton
public class RetailTicketRouting extends Routing<RetailTicketController> {

    @Inject
    public RetailTicketRouting() {
        super(RetailTicketController.class);
    }

    @Override
    public void bindRoutes() {
        path("/retail-tickets", List.of(Role.AUTHENTICATED), () -> {
            get(ctx -> controller().getAllRetailTickets(ctx));
            get("/categories", ctx -> controller().getAllCategories(ctx));
            get("/grouped-by-category", ctx -> controller().getAllGroupedByCategory(ctx));
            post(ctx -> controller().create(ctx));
            path("/{id}", () -> {
                get(ctx -> controller().getById(ctx));
                put(ctx -> controller().update(ctx));
                delete(ctx -> controller().delete(ctx));
            });
        });
    }
}
