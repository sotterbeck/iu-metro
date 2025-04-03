package de.sotterbeck.iumetro.infra.papermc.network;

import de.sotterbeck.iumetro.app.common.CommonPresenter;
import io.javalin.http.BadRequestResponse;

public class WebCommonPresenter implements CommonPresenter {

    @Override
    public void prepareFailView(String message) {
        throw new BadRequestResponse(message);
    }

}
