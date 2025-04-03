package de.sotterbeck.iumetro.app.faregate;

import java.time.ZonedDateTime;

public record UsageDto(
        String station,
        ZonedDateTime timeAtUsage,
        UsageType usageType
) {

}
