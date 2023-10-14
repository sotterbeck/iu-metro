package de.sotterbeck.iumetro.usecase.ticket;

public record UsageResponseModel(
        String station,
        String time,
        String usageType
) {

}
