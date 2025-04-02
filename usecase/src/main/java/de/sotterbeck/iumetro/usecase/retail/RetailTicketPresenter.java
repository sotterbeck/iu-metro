package de.sotterbeck.iumetro.usecase.retail;

public interface RetailTicketPresenter {

    RetailTicketResponseModel prepareSuccessView(RetailTicketRequestModel retailTicket);

    RetailTicketResponseModel prepareFailView(String message);

}
