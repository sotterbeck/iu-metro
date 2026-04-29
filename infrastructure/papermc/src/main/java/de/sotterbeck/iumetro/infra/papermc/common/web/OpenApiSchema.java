package de.sotterbeck.iumetro.infra.papermc.common.web;

import de.sotterbeck.iumetro.app.network.graph.MetroNetworkGraphService;
import de.sotterbeck.iumetro.app.network.line.LineResponseModel;
import de.sotterbeck.iumetro.app.retail.RetailTicketResponseModel;
import de.sotterbeck.iumetro.app.station.MetroStationResponseModel;

import java.util.List;
import java.util.Map;

/**
 * Wrapper records used exclusively for OpenAPI schema generation.
 * These mirror the exact JSON shapes returned by the API so that
 * the javalin-openapi annotation processor can generate accurate,
 * client-friendly schemas.
 */
public final class OpenApiSchema {

    public record ErrorResponse(String message) {

    }

    public record MessageResponse(String data) {

    }

    public record TokenResponse(String accessToken, long expiresIn) {

    }

    public record LinesResponse(List<LineResponseModel> data) {

    }

    public record NetworkGraphResponse(MetroNetworkGraphService.Response data) {

    }

    public record StationsResponse(List<MetroStationResponseModel> data) {

    }

    public record RetailTicketsResponse(List<RetailTicketResponseModel> data) {

    }

    public record RetailTicketResponse(RetailTicketResponseModel data) {

    }

    public record CategoriesResponse(List<String> data) {

    }

    public record GroupedTicketsResponse(Map<String, List<RetailTicketResponseModel>> data) {

    }

}
