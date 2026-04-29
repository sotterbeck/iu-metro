package de.sotterbeck.iumetro.infra.papermc.network;

import de.sotterbeck.iumetro.app.network.line.LineService;
import de.sotterbeck.iumetro.infra.papermc.common.web.ApiResponse;
import de.sotterbeck.iumetro.infra.papermc.common.web.OpenApiSchema;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.openapi.*;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class LineController {

    private final LineService lineService;

    @Inject
    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @OpenApi(
            path = "/api/lines",
            methods = HttpMethod.GET,
            summary = "List all lines",
            operationId = "getAllLines",
            tags = {"Lines"},
            security = {@OpenApiSecurity(name = "bearerAuth")},
            responses = {
                    @OpenApiResponse(status = "200", description = "List of lines", content = @OpenApiContent(from = OpenApiSchema.LinesResponse.class))
            }
    )
    public void getAllLines(Context ctx) {
        ctx.json(ApiResponse.success(lineService.getAllLines()));
    }

    @OpenApi(
            path = "/api/lines",
            methods = HttpMethod.POST,
            summary = "Create a line",
            operationId = "createLine",
            tags = {"Lines"},
            security = {@OpenApiSecurity(name = "bearerAuth")},
            requestBody = @OpenApiRequestBody(
                    required = true,
                    description = "Line data",
                    content = @OpenApiContent(from = LineService.LineRequestModel.class)
            ),
            responses = {
                    @OpenApiResponse(status = "201", description = "Line created successfully", content = @OpenApiContent(from = OpenApiSchema.MessageResponse.class)),
                    @OpenApiResponse(status = "400", description = "Invalid color or duplicate name", content = @OpenApiContent(from = OpenApiSchema.ErrorResponse.class))
            }
    )
    public void create(Context ctx) {
        LineService.LineRequestModel data = ctx.bodyAsClass(LineService.LineRequestModel.class);
        lineService.createLine(data);
        ctx.status(HttpStatus.CREATED);
        ctx.json(ApiResponse.success("Line created successfully"));
    }

    @OpenApi(
            path = "/api/lines/{name}",
            methods = HttpMethod.DELETE,
            summary = "Delete a line",
            operationId = "deleteLine",
            tags = {"Lines"},
            security = {@OpenApiSecurity(name = "bearerAuth")},
            pathParams = {
                    @OpenApiParam(name = "name", description = "Name of the line", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", description = "Line deleted successfully", content = @OpenApiContent(from = OpenApiSchema.MessageResponse.class)),
                    @OpenApiResponse(status = "400", description = "Line does not exist", content = @OpenApiContent(from = OpenApiSchema.ErrorResponse.class))
            }
    )
    public void delete(Context ctx) {
        String name = ctx.pathParam("name");
        lineService.removeLine(name);
        ctx.status(HttpStatus.OK);
        ctx.json(ApiResponse.success("Line deleted successfully"));
    }

}
