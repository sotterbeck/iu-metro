package de.sotterbeck.iumetro.infra.papermc.faregate;

import de.sotterbeck.iumetro.app.faregate.FareGateProtectionService;
import org.bukkit.Location;
import org.bukkit.block.data.type.Gate;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;

public class GateInteractListener implements Listener {

    private final FareGateProtectionService fareGateProtectionService;

    public GateInteractListener(FareGateProtectionService fareGateProtectionService) {
        this.fareGateProtectionService = fareGateProtectionService;
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
        FareGateProtectionService.Request request = new FareGateProtectionService.Request(location.getBlockX(), location.getBlockY(), location.getBlockZ(), orientation);

        boolean canOpenGate = fareGateProtectionService.canOpenGate(request);
        if (!canOpenGate) {
            event.setCancelled(true);
        }
    }

}
