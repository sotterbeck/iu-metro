package de.sotterbeck.iumetro.usecase.faregate;

public record UsageResponseModel(
        String station,
        String timeAtUsage,
        String usageType
) {

}
