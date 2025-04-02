package de.sotterbeck.iumetro.entrypoint.papermc.retail;

import de.sotterbeck.iumetro.usecase.retail.RetailTicketPresenter;
import de.sotterbeck.iumetro.usecase.retail.RetailTicketRequestModel;
import de.sotterbeck.iumetro.usecase.retail.RetailTicketResponseModel;
import io.javalin.http.BadRequestResponse;

public class WebRetailTicketPresenter implements RetailTicketPresenter {

    @Override
    public RetailTicketResponseModel prepareSuccessView(RetailTicketRequestModel retailTicket) {
        return new RetailTicketResponseModel(
                retailTicket.id(),
                retailTicket.name(),
                retailTicket.description(),
                retailTicket.priceCents(),
                retailTicket.usageLimit(),
                retailTicket.timeLimit(),
                retailTicket.isActive(),
                "",
                retailTicket.category()
        );
    }

    @Override
    public RetailTicketResponseModel prepareFailView(String message) {
        throw new BadRequestResponse(message);
    }

}
