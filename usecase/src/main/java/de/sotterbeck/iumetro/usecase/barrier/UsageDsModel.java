package de.sotterbeck.iumetro.usecase.barrier;

import java.time.ZonedDateTime;

public record UsageDsModel(
        String station,
        ZonedDateTime timeAtUsage,
        UsageType usageType
) {

}
