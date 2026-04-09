package de.sotterbeck.iumetro.infra.papermc.retail;

import de.sotterbeck.iumetro.app.retail.RetailTicketPresenter;
import de.sotterbeck.iumetro.app.retail.RetailTicketRequestModel;
import de.sotterbeck.iumetro.app.retail.RetailTicketResponseModel;
import io.javalin.http.BadRequestResponse;

import java.time.ZonedDateTime;

public class WebRetailTicketPresenter implements RetailTicketPresenter {

    @Override
    public RetailTicketResponseModel prepareSuccessView(RetailTicketRequestModel retailTicket) {
        return new RetailTicketResponseModel(
                retailTicket.id(),
                retailTicket.name(),
                retailTicket.description(),
                retailTicket.priceCents(),
                retailTicket.config(),
                retailTicket.isActive(),
                ZonedDateTime.now().toString(),
                retailTicket.category()
        );
    }

    @Override
    public RetailTicketResponseModel prepareFailView(String message) {
        throw new BadRequestResponse(message);
    }

}
