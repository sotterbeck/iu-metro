package de.sotterbeck.iumetro.usecase.retail;

public record RetailTicketRequestModel(
        String id,
        String name,
        String description,
        long priceCents,
        int usageLimit,
        String timeLimit,
        boolean isActive,
        String category
) {

}
