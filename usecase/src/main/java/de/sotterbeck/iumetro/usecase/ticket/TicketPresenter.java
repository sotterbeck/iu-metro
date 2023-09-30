package de.sotterbeck.iumetro.usecase.ticket;

public interface TicketPresenter {

    void prepareSuccessView(TicketRequestModel ticket, String message);

    void prepareFailView(String message);

    void printTicket(TicketRequestModel ticket);

}
