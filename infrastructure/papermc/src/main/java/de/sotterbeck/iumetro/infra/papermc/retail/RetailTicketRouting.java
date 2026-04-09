package de.sotterbeck.iumetro.infra.papermc.retail;

import de.sotterbeck.iumetro.app.retail.RetailTicketRequestModel;
import de.sotterbeck.iumetro.app.retail.RetailTicketResponseModel;
import de.sotterbeck.iumetro.app.retail.RetailTicketService;
import de.sotterbeck.iumetro.infra.papermc.common.web.Routing;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;

import java.util.List;
import java.util.Optional;

public class RetailTicketRouting implements Routing {

    private final Javalin javalin;
    private final RetailTicketService retailTicketService;

    public RetailTicketRouting(Javalin javalin, RetailTicketService retailTicketService) {
        this.javalin = javalin;
        this.retailTicketService = retailTicketService;
    }

    @Override
    public void bindRoutes() {
        javalin.get("/api/retail-tickets", ctx -> {
            List<RetailTicketResponseModel> tickets = retailTicketService.getAll();
            ctx.json(tickets);
        });

        javalin.get("/api/retail-tickets/{id}", ctx -> {
            String id = ctx.pathParam("id");

            Optional<RetailTicketResponseModel> ticket = retailTicketService.getById(id);

            if (ticket.isEmpty()) {
                ctx.status(HttpStatus.NOT_FOUND);
                return;
            }

            ctx.json(ticket.get());
        });

        javalin.post("/api/retail-tickets", ctx -> {
            RetailTicketRequestModel request = ctx.bodyAsClass(RetailTicketRequestModel.class);
            RetailTicketResponseModel response = retailTicketService.create(request);
            ctx.status(HttpStatus.CREATED);
            ctx.json(response);
        });

        javalin.put("/api/retail-tickets/{id}", ctx -> {
            String id = ctx.pathParam("id");
            RetailTicketRequestModel request = ctx.bodyAsClass(RetailTicketRequestModel.class);
            RetailTicketResponseModel response = retailTicketService.update(id, request);
            ctx.json(response);
        });

        javalin.delete("/api/retail-tickets/{id}", ctx -> {
            String id = ctx.pathParam("id");
            RetailTicketResponseModel response = retailTicketService.delete(id);
            ctx.json(response);
        });
    }

}
