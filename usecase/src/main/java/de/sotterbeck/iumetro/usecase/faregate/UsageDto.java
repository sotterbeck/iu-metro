package de.sotterbeck.iumetro.usecase.faregate;

import java.time.ZonedDateTime;

public record UsageDto(
        String station,
        ZonedDateTime timeAtUsage,
        UsageType usageType
) {

}
