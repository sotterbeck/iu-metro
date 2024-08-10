package de.sotterbeck.iumetro.usecase.faregate;

import java.time.ZonedDateTime;

public record UsageRequestModel(
        String station,
        ZonedDateTime timeAtUsage,
        UsageType usageType

) {

}
