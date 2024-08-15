package de.sotterbeck.iumetro.entrypoint.papermc.faregate;

import de.sotterbeck.iumetro.usecase.faregate.FareGateProtectionInteractor;
import de.sotterbeck.iumetro.usecase.faregate.GateRequestModel;
import org.bukkit.Location;
import org.bukkit.block.data.type.Gate;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;

public class GateInteractListener implements Listener {

    private final FareGateProtectionInteractor fareGateProtectionInteractor;

    public GateInteractListener(FareGateProtectionInteractor fareGateProtectionInteractor) {
        this.fareGateProtectionInteractor = fareGateProtectionInteractor;
    }

    @EventHandler
    public void onFenceGateInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK
                || !(Objects.requireNonNull(event.getClickedBlock()).getBlockData() instanceof Gate gate)
        ) {
            return;
        }

        Location location = event.getClickedBlock().getLocation();
        String orientation = gate.getFacing().toString();
        GateRequestModel request = new GateRequestModel(location.getBlockX(), location.getBlockY(), location.getBlockZ(), orientation);

        boolean canOpenGate = fareGateProtectionInteractor.canOpenGate(request);
        if (!canOpenGate) {
            event.setCancelled(true);
        }
    }

}
