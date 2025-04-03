package de.sotterbeck.iumetro.app.faregate;

public record UsageResponseModel(
        String station,
        String timeAtUsage,
        String usageType
) {

}
