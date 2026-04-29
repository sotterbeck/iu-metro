package de.sotterbeck.iumetro.infra.papermc.retail;

import de.sotterbeck.iumetro.infra.papermc.auth.Role;
import de.sotterbeck.iumetro.infra.papermc.common.web.Routing;
import io.javalin.Javalin;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class RetailTicketRouting extends Routing<RetailTicketController> {

    private final Javalin javalin;

    @Inject
    public RetailTicketRouting(Javalin javalin) {
        super(RetailTicketController.class);
        this.javalin = javalin;
    }

    @Override
    public void bindRoutes() {
        javalin.get("/api/retail-tickets", ctx -> controller().getAllRetailTickets(ctx), Role.AUTHENTICATED);
        javalin.get("/api/retail-tickets/categories", ctx -> controller().getAllCategories(ctx), Role.AUTHENTICATED);
        javalin.get("/api/retail-tickets/grouped-by-category", ctx -> controller().getAllGroupedByCategory(ctx), Role.AUTHENTICATED);
        javalin.get("/api/retail-tickets/{id}", ctx -> controller().getById(ctx), Role.AUTHENTICATED);
        javalin.post("/api/retail-tickets", ctx -> controller().create(ctx), Role.AUTHENTICATED);
        javalin.put("/api/retail-tickets/{id}", ctx -> controller().update(ctx), Role.AUTHENTICATED);
        javalin.delete("/api/retail-tickets/{id}", ctx -> controller().delete(ctx), Role.AUTHENTICATED);
    }
}
