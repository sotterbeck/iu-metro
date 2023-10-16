package de.sotterbeck.iumetro.usecase.barrier;

import java.time.ZonedDateTime;

public record UsageRequestModel(
        String station,
        ZonedDateTime timeAtUsage,
        UsageType usageType

) {

}
