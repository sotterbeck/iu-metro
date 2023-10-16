package de.sotterbeck.iumetro.usecase.barrier;

import de.sotterbeck.iumetro.usecase.ticket.UsageType;

import java.time.ZonedDateTime;

public record UsageRequestModel(
        String station,
        ZonedDateTime timeAtUsage,
        UsageType usageType

) {

}
