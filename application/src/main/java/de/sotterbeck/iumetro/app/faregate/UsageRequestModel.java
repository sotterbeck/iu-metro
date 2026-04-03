package de.sotterbeck.iumetro.app.faregate;

import java.time.ZonedDateTime;
import java.util.UUID;

public record UsageRequestModel(
        //** The player that is using the fare gate. */
        UUID playerId,
        //** The station name that is used. */
        String station,
        //** The time at which the player used the fare gate. */
        ZonedDateTime timeAtUsage,
        //** The type of usage. */
        UsageType usageType
) {

}
