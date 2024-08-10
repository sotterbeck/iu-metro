package de.sotterbeck.iumetro.entrypoint.papermc.faregate.sign;

import de.sotterbeck.iumetro.entrypoint.papermc.common.sign.SignClickEvent;
import de.sotterbeck.iumetro.entrypoint.papermc.common.sign.SignClickHandler;
import de.sotterbeck.iumetro.usecase.faregate.FareGateControlInteractor;
import de.sotterbeck.iumetro.usecase.faregate.FareGateControlRequestModel;
import de.sotterbeck.iumetro.usecase.faregate.PositionDto;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;

public class FareGateSignClickHandler implements SignClickHandler {

    private final FareGateControlInteractor fareGateControlInteractor;

    public FareGateSignClickHandler(FareGateControlInteractor fareGateControlInteractor) {
        this.fareGateControlInteractor = fareGateControlInteractor;
    }

    @Override
    public void onRightClick(SignClickEvent event) {
        Sign sign = event.sign();
        if (!(sign.getBlockData() instanceof WallSign wallSign)) {
            return;
        }

        String orientation = wallSign.getFacing().toString();
        PositionDto position = new PositionDto(sign.getX(), sign.getY(), sign.getZ());
        FareGateControlRequestModel request = new FareGateControlRequestModel(position, orientation);

        fareGateControlInteractor.openGate(request);
    }

}
