package de.sotterbeck.iumetro.app.ticket;

import java.util.UUID;

public record TicketRequestModel(UUID id, String name, TicketConfig config) {

}
