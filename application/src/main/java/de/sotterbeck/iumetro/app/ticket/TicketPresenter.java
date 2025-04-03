package de.sotterbeck.iumetro.app.ticket;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface TicketPresenter {

    @NotNull TicketResponseModel prepareSuccessView(TicketRequestModel ticket);

    @Nullable TicketResponseModel prepareFailView(String message);

}
