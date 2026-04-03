package de.sotterbeck.iumetro.infra.papermc.faregate.sign;

import de.sotterbeck.iumetro.app.common.PositionDto;
import de.sotterbeck.iumetro.app.faregate.FareGateControlRequestModel;
import de.sotterbeck.iumetro.app.faregate.FareGateControlService;
import de.sotterbeck.iumetro.app.faregate.UsageRequestModel;
import de.sotterbeck.iumetro.app.faregate.UsageType;
import de.sotterbeck.iumetro.app.ticket.TicketItemRepository;
import de.sotterbeck.iumetro.infra.papermc.common.Components;
import de.sotterbeck.iumetro.infra.papermc.common.sign.SignClickEvent;
import de.sotterbeck.iumetro.infra.papermc.common.sign.SignClickHandler;
import de.sotterbeck.iumetro.infra.papermc.faregate.FareGateKeyFactory;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.time.ZonedDateTime;
import java.util.Objects;

public class FareGateSignClickHandler implements SignClickHandler {

    private final FareGateControlService fareGateControlService;
    private final FareGateKeyFactory fareGateKeyFactory;
    private final TicketItemRepository ticketItemRepository;

    public FareGateSignClickHandler(FareGateControlService fareGateControlService,
                                    FareGateKeyFactory fareGateKeyFactory,
                                    TicketItemRepository ticketItemRepository) {
        this.fareGateControlService = fareGateControlService;
        this.fareGateKeyFactory = fareGateKeyFactory;
        this.ticketItemRepository = ticketItemRepository;
    }

    @Override
    public void onRightClick(SignClickEvent event) {
        Sign sign = event.sign();
        if (!(sign.getBlockData() instanceof WallSign wallSign)) {
            return;
        }

        String orientation = wallSign.getFacing().toString();
        PositionDto position = new PositionDto(sign.getX(), sign.getY(), sign.getZ());
        UsageRequestModel usageRequest = createUsageRequest(event);
        FareGateControlRequestModel request = new FareGateControlRequestModel(position, orientation, usageRequest);

        fareGateControlService.controlGate(request);
    }

    @Override
    public void onLeftClick(SignClickEvent event) {
        if (!event.player().hasPermission("iumetro.admin")) {
            return;
        }
        UsageRequestModel usageRequest = createUsageRequest(event);

        String debugInfo = "<gold><bold>[DEBUG]</bold><yellow> type: <white>%s <yellow>station: <white>%s".formatted(usageRequest.usageType(), usageRequest.station());

        event.player().sendActionBar(Components.mm(debugInfo));
    }

    private UsageRequestModel createUsageRequest(SignClickEvent event) {
        Sign sign = event.sign();
        PersistentDataContainer container = sign.getPersistentDataContainer();

        var playerId = event.player().getUniqueId();

        String typeStr = container.get(fareGateKeyFactory.getFareGateTypeKey(), PersistentDataType.STRING);
        String station = container.get(fareGateKeyFactory.getStationKey(), PersistentDataType.STRING);

        UsageType type = Objects.equals(typeStr, "entry") ? UsageType.ENTRY : UsageType.EXIT;

        return new UsageRequestModel(playerId, station, ZonedDateTime.now(), type);
    }

}
