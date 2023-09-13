package de.sotterbeck.uimetro.usecase.ticket;

import java.time.Duration;
import java.util.UUID;

public record TicketDsRequestModel(UUID id, String name, int usageLimit, Duration timeLimit) {

}
