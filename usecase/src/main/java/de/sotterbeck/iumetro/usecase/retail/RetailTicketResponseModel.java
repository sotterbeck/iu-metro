package de.sotterbeck.iumetro.usecase.retail;

public record RetailTicketResponseModel(
        String id,
        String name,
        String description,
        long priceCents,
        int usageLimit,
        String timeLimit,
        boolean isActive,
        String createdAt,
        String category
) {

}
