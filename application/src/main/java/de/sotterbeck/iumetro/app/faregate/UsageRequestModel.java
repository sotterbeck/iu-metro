package de.sotterbeck.iumetro.app.faregate;

import java.time.ZonedDateTime;

public record UsageRequestModel(
        String station,
        ZonedDateTime timeAtUsage,
        UsageType usageType

) {

}
