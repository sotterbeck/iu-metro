package de.sotterbeck.iumetro.app.ticket;

import java.util.UUID;

public record TicketDto(UUID id, String name, TicketConfig config) {

}
