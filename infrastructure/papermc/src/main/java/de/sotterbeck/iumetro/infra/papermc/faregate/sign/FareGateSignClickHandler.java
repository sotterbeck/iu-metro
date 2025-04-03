package de.sotterbeck.iumetro.infra.papermc.faregate.sign;

import de.sotterbeck.iumetro.app.common.PositionDto;
import de.sotterbeck.iumetro.app.faregate.FareGateControlInteractor;
import de.sotterbeck.iumetro.app.faregate.FareGateControlRequestModel;
import de.sotterbeck.iumetro.infra.papermc.common.Components;
import de.sotterbeck.iumetro.infra.papermc.common.sign.SignClickEvent;
import de.sotterbeck.iumetro.infra.papermc.common.sign.SignClickHandler;
import de.sotterbeck.iumetro.infra.papermc.faregate.FareGateKeyFactory;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class FareGateSignClickHandler implements SignClickHandler {

    private final FareGateControlInteractor fareGateControlInteractor;
    private final FareGateKeyFactory fareGateKeyFactory;

    public FareGateSignClickHandler(FareGateControlInteractor fareGateControlInteractor, FareGateKeyFactory fareGateKeyFactory) {
        this.fareGateControlInteractor = fareGateControlInteractor;
        this.fareGateKeyFactory = fareGateKeyFactory;
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

    @Override
    public void onLeftClick(SignClickEvent event) {
        if (!event.player().hasPermission("iumetro.admin")) {
            return;
        }
        Sign sign = event.sign();
        PersistentDataContainer container = sign.getPersistentDataContainer();

        String type = container.get(fareGateKeyFactory.getFareGateTypeKey(), PersistentDataType.STRING);
        String stationId = container.get(fareGateKeyFactory.getStationKey(), PersistentDataType.STRING);

        String debugInfo = "<gold><bold>[DEBUG]</bold><yellow> type: <white>%s <yellow>stationId: <white>%s".formatted(type, stationId);

        event.player().sendActionBar(Components.mm(debugInfo));
    }

}
