package de.sotterbeck.iumetro.usecase.ticket;

public interface TicketPresenter {

    void prepareSuccessView(TicketRequestModel ticket);

    void prepareFailView(String message);

    void printTicket(TicketRequestModel ticket);

}
