package de.sotterbeck.iumetro.infra.papermc.retail;

import de.sotterbeck.iumetro.app.retail.RetailTicketRequestModel;
import de.sotterbeck.iumetro.app.retail.RetailTicketResponseModel;
import de.sotterbeck.iumetro.app.retail.RetailTicketService;
import de.sotterbeck.iumetro.infra.papermc.common.web.ApiResponse;
import de.sotterbeck.iumetro.infra.papermc.common.web.Routing;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;

import java.util.List;
import java.util.Map;
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
            ctx.json(ApiResponse.success(tickets));
        });

        javalin.get("/api/retail-tickets/categories", ctx -> {
            List<String> categories = retailTicketService.getAllCategories();
            ctx.json(ApiResponse.success(categories));
        });

        javalin.get("/api/retail-tickets/grouped-by-category", ctx -> {
            Map<String, List<RetailTicketResponseModel>> grouped = retailTicketService.getAllGroupedByCategory();
            ctx.json(ApiResponse.success(grouped));
        });

        javalin.get("/api/retail-tickets/{id}", ctx -> {
            String id = ctx.pathParam("id");

            Optional<RetailTicketResponseModel> ticket = retailTicketService.getById(id);

            if (ticket.isEmpty()) {
                ctx.status(HttpStatus.NOT_FOUND);
                ctx.json(ApiResponse.<RetailTicketResponseModel>failure("Ticket not found"));
                return;
            }

            ctx.json(ApiResponse.success(ticket.get()));
        });

        javalin.post("/api/retail-tickets", ctx -> {
            RetailTicketRequestModel request = ctx.bodyAsClass(RetailTicketRequestModel.class);
            RetailTicketResponseModel response = retailTicketService.create(request);
            ctx.status(HttpStatus.CREATED);
            ctx.json(ApiResponse.success(response));
        });

        javalin.put("/api/retail-tickets/{id}", ctx -> {
            String id = ctx.pathParam("id");
            RetailTicketRequestModel request = ctx.bodyAsClass(RetailTicketRequestModel.class);
            RetailTicketResponseModel response = retailTicketService.update(id, request);
            ctx.json(ApiResponse.success(response));
        });

        javalin.delete("/api/retail-tickets/{id}", ctx -> {
            String id = ctx.pathParam("id");
            RetailTicketResponseModel response = retailTicketService.delete(id);
            ctx.json(ApiResponse.success(response));
        });
    }

}
