package de.sotterbeck.iumetro.entrypoint.papermc.network;

import de.sotterbeck.iumetro.usecase.common.CommonPresenter;
import io.javalin.http.BadRequestResponse;

public class WebCommonPresenter implements CommonPresenter {

    @Override
    public void prepareFailView(String message) {
        throw new BadRequestResponse(message);
    }

}
