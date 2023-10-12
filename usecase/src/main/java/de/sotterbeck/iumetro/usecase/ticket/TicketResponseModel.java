package de.sotterbeck.iumetro.usecase.ticket;

public record TicketResponseModel(
        String fullId,
        String shortId,
        String name,
        String usageLimit,
        String timeLimit
) {

}
