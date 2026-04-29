package de.sotterbeck.iumetro.infra.papermc.station.web;

import de.sotterbeck.iumetro.app.station.MetroStationModificationService;
import de.sotterbeck.iumetro.app.station.MetroStationModificationService.Status;
import de.sotterbeck.iumetro.app.station.MetroStationService;
import de.sotterbeck.iumetro.infra.papermc.common.web.ApiResponse;
import de.sotterbeck.iumetro.infra.papermc.common.web.OpenApiSchema;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.openapi.*;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.List;

@Singleton
public class MetroStationController {

    private final MetroStationService metroStationService;
    private final MetroStationModificationService metroStationModificationService;

    @Inject
    public MetroStationController(MetroStationService metroStationService, MetroStationModificationService metroStationModificationService) {
        this.metroStationService = metroStationService;
        this.metroStationModificationService = metroStationModificationService;
    }

    @OpenApi(
            path = "/api/metro-stations",
            methods = HttpMethod.GET,
            summary = "List all stations",
            operationId = "getAllStations",
            tags = {"Metro Stations"},
            security = {@OpenApiSecurity(name = "bearerAuth")},
            responses = {
                    @OpenApiResponse(status = "200", description = "List of stations", content = @OpenApiContent(from = OpenApiSchema.StationsResponse.class))
            }
    )
    public void getAll(Context ctx) {
        ctx.json(ApiResponse.success(metroStationService.getAll()));
    }

    @OpenApi(
            path = "/api/metro-stations/positioned",
            methods = HttpMethod.GET,
            summary = "List positioned stations",
            operationId = "getAllPositionedStations",
            tags = {"Metro Stations"},
            security = {@OpenApiSecurity(name = "bearerAuth")},
            responses = {
                    @OpenApiResponse(status = "200", description = "List of stations with positions", content = @OpenApiContent(from = OpenApiSchema.StationsResponse.class))
            }
    )
    public void getAllPositioned(Context ctx) {
        ctx.json(ApiResponse.success(metroStationService.getAllPositioned()));
    }

    @OpenApi(
            path = "/api/metro-stations/{name}/lines",
            methods = HttpMethod.PUT,
            summary = "Save station lines",
            operationId = "saveStationLines",
            tags = {"Metro Stations"},
            description = "Updates the lines assigned to a metro station.",
            security = {@OpenApiSecurity(name = "bearerAuth")},
            pathParams = {
                    @OpenApiParam(name = "name", description = "Name of the station", required = true)
            },
            requestBody = @OpenApiRequestBody(
                    required = true,
                    description = "List of line names",
                    content = @OpenApiContent(from = SaveLinesBody.class)
            ),
            responses = {
                    @OpenApiResponse(status = "200", description = "Lines saved successfully", content = @OpenApiContent(from = OpenApiSchema.MessageResponse.class)),
                    @OpenApiResponse(status = "404", description = "Station not found", content = @OpenApiContent(from = OpenApiSchema.ErrorResponse.class))
            }
    )
    public void saveLines(Context ctx) {
        String name = ctx.pathParam("name");
        var body = ctx.bodyAsClass(SaveLinesBody.class);

        var status = metroStationModificationService.saveLines(name, body.lines());

        if (status == Status.NOT_FOUND) {
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.json(ApiResponse.failure("Station not found"));
            return;
        }

        ctx.status(HttpStatus.OK);
        ctx.json(ApiResponse.success("Lines saved successfully"));
    }

    record SaveLinesBody(List<String> lines) {

    }

}
