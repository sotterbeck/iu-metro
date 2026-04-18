package de.sotterbeck.iumetro.infra.papermc.retail;

import de.sotterbeck.iumetro.app.retail.RetailTicketDto;
import de.sotterbeck.iumetro.app.retail.RetailTicketPresenter;
import de.sotterbeck.iumetro.app.retail.RetailTicketResponseModel;
import io.javalin.http.BadRequestResponse;

public class WebRetailTicketPresenter implements RetailTicketPresenter {

    @Override
    public RetailTicketResponseModel prepareSuccessView(RetailTicketDto retailTicket) {
        return new RetailTicketResponseModel(
                retailTicket.id().toString(),
                retailTicket.name(),
                retailTicket.description(),
                retailTicket.priceCents(),
                retailTicket.config(),
                retailTicket.isActive(),
                retailTicket.createdAt().toString(),
                retailTicket.category()
        );
    }

    @Override
    public RetailTicketResponseModel prepareFailView(String message) {
        throw new BadRequestResponse(message);
    }

}
