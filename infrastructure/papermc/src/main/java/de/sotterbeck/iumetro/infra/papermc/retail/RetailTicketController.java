package de.sotterbeck.iumetro.infra.papermc.retail;

import de.sotterbeck.iumetro.app.retail.RetailTicketRequestModel;
import de.sotterbeck.iumetro.app.retail.RetailTicketResponseModel;
import de.sotterbeck.iumetro.app.retail.RetailTicketService;
import de.sotterbeck.iumetro.infra.papermc.common.web.ApiResponse;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Singleton
public class RetailTicketController {

    private final RetailTicketService retailTicketService;

    @Inject
    public RetailTicketController(RetailTicketService retailTicketService) {
        this.retailTicketService = retailTicketService;
    }

    public void getAllRetailTickets(Context ctx) {
        List<RetailTicketResponseModel> tickets = retailTicketService.getAll();
        ctx.json(ApiResponse.success(tickets));
    }

    public void getAllCategories(Context ctx) {
        List<String> categories = retailTicketService.getAllCategories();
        ctx.json(ApiResponse.success(categories));
    }

    public void getAllGroupedByCategory(Context ctx) {
        Map<String, List<RetailTicketResponseModel>> grouped = retailTicketService.getAllGroupedByCategory();
        ctx.json(ApiResponse.success(grouped));
    }

    public void getById(Context ctx) {
        String id = ctx.pathParam("id");

        Optional<RetailTicketResponseModel> ticket = retailTicketService.getById(id);

        if (ticket.isEmpty()) {
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.json(ApiResponse.<RetailTicketResponseModel>failure("Ticket not found"));
            return;
        }

        ctx.json(ApiResponse.success(ticket.get()));
    }

    public void create(Context ctx) {
        RetailTicketRequestModel request = ctx.bodyAsClass(RetailTicketRequestModel.class);
        RetailTicketResponseModel response = retailTicketService.create(request);
        ctx.status(HttpStatus.CREATED);
        ctx.json(ApiResponse.success(response));
    }

    public void update(Context ctx) {
        String id = ctx.pathParam("id");
        RetailTicketRequestModel request = ctx.bodyAsClass(RetailTicketRequestModel.class);
        RetailTicketResponseModel response = retailTicketService.update(id, request);
        ctx.json(ApiResponse.success(response));
    }

    public void delete(Context ctx) {
        String id = ctx.pathParam("id");
        RetailTicketResponseModel response = retailTicketService.delete(id);
        ctx.json(ApiResponse.success(response));
    }

}
