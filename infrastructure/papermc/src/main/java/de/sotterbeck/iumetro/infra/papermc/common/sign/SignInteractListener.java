package de.sotterbeck.iumetro.infra.papermc.common.sign;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

public class SignInteractListener implements Listener {

    private final NamespacedKey signTypeKey;
    private final SignClickHandlerFactory signClickHandlerFactory;

    public SignInteractListener(NamespacedKey signTypeKey, SignClickHandlerFactory signClickHandlerFactory) {
        this.signTypeKey = signTypeKey;
        this.signClickHandlerFactory = signClickHandlerFactory;
    }

    @EventHandler
    public void onSignInteract(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null
                || !(clickedBlock.getState() instanceof Sign sign)
                || !sign.getPersistentDataContainer().has(signTypeKey, PersistentDataType.STRING)) {
            return;
        }

        if (!isInteractingFromFront(event.getPlayer(), clickedBlock)) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);
        SignClickHandler handler = signClickHandlerFactory.create(sign);
        SignClickEvent clickEvent = new SignClickEvent(sign, event.getPlayer());

        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_BLOCK) {
            handler.onRightClick(clickEvent);
        } else if (action == Action.LEFT_CLICK_BLOCK) {
            handler.onLeftClick(clickEvent);
        }
    }

    private boolean isInteractingFromFront(Player player, Block signBlock) {
        BlockData blockData = signBlock.getBlockData();

        if (!(blockData instanceof Directional directional)) {
            return false;
        }

        Vector playerDirection = player.getLocation().getDirection();
        Vector signFacingDirection = directional.getFacing().getDirection();

        playerDirection.normalize();
        signFacingDirection.normalize();

        double dotProduct = playerDirection.dot(signFacingDirection);
        return dotProduct < 0;
    }

}
