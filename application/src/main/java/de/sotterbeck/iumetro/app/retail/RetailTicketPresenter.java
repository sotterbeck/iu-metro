package de.sotterbeck.iumetro.app.retail;

public interface RetailTicketPresenter {

    RetailTicketResponseModel prepareSuccessView(RetailTicketDto retailTicket);

    RetailTicketResponseModel prepareFailView(String message);

}
