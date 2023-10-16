package de.sotterbeck.iumetro.usecase.barrier;

public record UsageResponseModel(
        String station,
        String timeAtUsage,
        String usageType
) {

}
