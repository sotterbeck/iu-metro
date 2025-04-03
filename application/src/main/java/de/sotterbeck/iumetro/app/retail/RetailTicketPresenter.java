package de.sotterbeck.iumetro.app.retail;

public interface RetailTicketPresenter {

    RetailTicketResponseModel prepareSuccessView(RetailTicketRequestModel retailTicket);

    RetailTicketResponseModel prepareFailView(String message);

}
