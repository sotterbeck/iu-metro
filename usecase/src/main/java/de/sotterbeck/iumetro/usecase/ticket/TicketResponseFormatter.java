package de.sotterbeck.iumetro.usecase.ticket;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class TicketResponseFormatter implements TicketPresenter {

    @Override
    public @NotNull TicketResponseModel prepareSuccessView(TicketRequestModel ticket) {
        UUID id = ticket.id();
        return new TicketResponseModel(
                id.toString(),
                formatShortId(id),
                ticket.name(),
                String.valueOf(ticket.usageLimit()),
                ticket.timeLimit().toString()
        );
    }

    @Override
    public @Nullable TicketResponseModel prepareFailView(String message) {
        return null;
    }

    protected String formatShortId(UUID id) {
        return id.toString().substring(0, 8);
    }

}
