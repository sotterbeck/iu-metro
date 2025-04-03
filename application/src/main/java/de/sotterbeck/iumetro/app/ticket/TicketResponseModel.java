package de.sotterbeck.iumetro.app.ticket;

public record TicketResponseModel(
        String fullId,
        String shortId,
        String name,
        String usageLimit,
        String timeLimit
) {

}
