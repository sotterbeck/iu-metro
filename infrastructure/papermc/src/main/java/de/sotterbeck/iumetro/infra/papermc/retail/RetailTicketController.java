package de.sotterbeck.iumetro.infra.papermc.retail;

import de.sotterbeck.iumetro.app.retail.RetailTicketRequestModel;
import de.sotterbeck.iumetro.app.retail.RetailTicketResponseModel;
import de.sotterbeck.iumetro.app.retail.RetailTicketService;
import de.sotterbeck.iumetro.infra.papermc.common.web.ApiResponse;
import de.sotterbeck.iumetro.infra.papermc.common.web.OpenApiSchema;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.openapi.*;
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

    @OpenApi(
            path = "/api/retail-tickets",
            methods = HttpMethod.GET,
            summary = "List all retail tickets",
            operationId = "getAllRetailTickets",
            tags = {"Retail Tickets"},
            security = {@OpenApiSecurity(name = "bearerAuth")},
            responses = {
                    @OpenApiResponse(status = "200", description = "List of retail tickets", content = @OpenApiContent(from = OpenApiSchema.RetailTicketsResponse.class))
            }
    )
    public void getAllRetailTickets(Context ctx) {
        List<RetailTicketResponseModel> tickets = retailTicketService.getAll();
        ctx.json(ApiResponse.success(tickets));
    }

    @OpenApi(
            path = "/api/retail-tickets/categories",
            methods = HttpMethod.GET,
            summary = "List all ticket categories",
            operationId = "getAllCategories",
            tags = {"Retail Tickets"},
            security = {@OpenApiSecurity(name = "bearerAuth")},
            responses = {
                    @OpenApiResponse(status = "200", description = "List of categories", content = @OpenApiContent(from = OpenApiSchema.CategoriesResponse.class))
            }
    )
    public void getAllCategories(Context ctx) {
        List<String> categories = retailTicketService.getAllCategories();
        ctx.json(ApiResponse.success(categories));
    }

    @OpenApi(
            path = "/api/retail-tickets/grouped-by-category",
            methods = HttpMethod.GET,
            summary = "Get tickets grouped by category",
            operationId = "getAllGroupedByCategory",
            tags = {"Retail Tickets"},
            security = {@OpenApiSecurity(name = "bearerAuth")},
            responses = {
                    @OpenApiResponse(status = "200", description = "Tickets grouped by category", content = @OpenApiContent(from = OpenApiSchema.GroupedTicketsResponse.class))
            }
    )
    public void getAllGroupedByCategory(Context ctx) {
        Map<String, List<RetailTicketResponseModel>> grouped = retailTicketService.getAllGroupedByCategory();
        ctx.json(ApiResponse.success(grouped));
    }

    @OpenApi(
            path = "/api/retail-tickets/{id}",
            methods = HttpMethod.GET,
            summary = "Get a retail ticket by ID",
            operationId = "getRetailTicketById",
            tags = {"Retail Tickets"},
            security = {@OpenApiSecurity(name = "bearerAuth")},
            pathParams = {
                    @OpenApiParam(name = "id", description = "Ticket ID", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", description = "Retail ticket", content = @OpenApiContent(from = OpenApiSchema.RetailTicketResponse.class)),
                    @OpenApiResponse(status = "404", description = "Ticket not found", content = @OpenApiContent(from = OpenApiSchema.ErrorResponse.class))
            }
    )
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

    @OpenApi(
            path = "/api/retail-tickets",
            methods = HttpMethod.POST,
            summary = "Create a retail ticket",
            operationId = "createRetailTicket",
            tags = {"Retail Tickets"},
            security = {@OpenApiSecurity(name = "bearerAuth")},
            requestBody = @OpenApiRequestBody(
                    required = true,
                    description = "Retail ticket data",
                    content = @OpenApiContent(from = RetailTicketRequestModel.class)
            ),
            responses = {
                    @OpenApiResponse(status = "201", description = "Created retail ticket", content = @OpenApiContent(from = OpenApiSchema.RetailTicketResponse.class))
            }
    )
    public void create(Context ctx) {
        RetailTicketRequestModel request = ctx.bodyAsClass(RetailTicketRequestModel.class);
        RetailTicketResponseModel response = retailTicketService.create(request);
        ctx.status(HttpStatus.CREATED);
        ctx.json(ApiResponse.success(response));
    }

    @OpenApi(
            path = "/api/retail-tickets/{id}",
            methods = HttpMethod.PUT,
            summary = "Update a retail ticket",
            operationId = "updateRetailTicket",
            tags = {"Retail Tickets"},
            security = {@OpenApiSecurity(name = "bearerAuth")},
            pathParams = {
                    @OpenApiParam(name = "id", description = "Ticket ID", required = true)
            },
            requestBody = @OpenApiRequestBody(
                    required = true,
                    description = "Updated retail ticket data",
                    content = @OpenApiContent(from = RetailTicketRequestModel.class)
            ),
            responses = {
                    @OpenApiResponse(status = "200", description = "Updated retail ticket", content = @OpenApiContent(from = OpenApiSchema.RetailTicketResponse.class))
            }
    )
    public void update(Context ctx) {
        String id = ctx.pathParam("id");
        RetailTicketRequestModel request = ctx.bodyAsClass(RetailTicketRequestModel.class);
        RetailTicketResponseModel response = retailTicketService.update(id, request);
        ctx.json(ApiResponse.success(response));
    }

    @OpenApi(
            path = "/api/retail-tickets/{id}",
            methods = HttpMethod.DELETE,
            summary = "Delete a retail ticket",
            operationId = "deleteRetailTicket",
            tags = {"Retail Tickets"},
            security = {@OpenApiSecurity(name = "bearerAuth")},
            pathParams = {
                    @OpenApiParam(name = "id", description = "Ticket ID", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", description = "Deleted retail ticket", content = @OpenApiContent(from = OpenApiSchema.RetailTicketResponse.class))
            }
    )
    public void delete(Context ctx) {
        String id = ctx.pathParam("id");
        RetailTicketResponseModel response = retailTicketService.delete(id);
        ctx.json(ApiResponse.success(response));
    }

}
