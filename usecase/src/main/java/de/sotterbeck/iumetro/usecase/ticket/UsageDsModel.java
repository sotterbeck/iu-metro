package de.sotterbeck.iumetro.usecase.ticket;

import java.time.LocalDateTime;

public record UsageDsModel(
        String station,
        LocalDateTime time,
        UsageType usageType
) {

}
