package de.sotterbeck.iumetro.entrypoint.papermc.common.sign;

import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class SignPlaceListener implements Listener {

    private final SignInitializerFactory signInitializerFactory;
    private final NamespacedKey signTypeNamespacedKey;

    public SignPlaceListener(SignTypeKeyFactory signTypeKeyFactory, SignInitializerFactory signInitializerFactory) {
        this.signInitializerFactory = signInitializerFactory;
        signTypeNamespacedKey = signTypeKeyFactory.getSignTypeNamespacedKey();
    }

    @EventHandler
    public void onSignPlace(BlockPlaceEvent event) {
        BlockState blockState = event.getBlock().getState();
        if (!(blockState instanceof Sign sign)) {
            return;
        }

        ItemStack item = event.getItemInHand();
        ItemMeta meta = item.getItemMeta();

        if (meta != null && meta.getPersistentDataContainer().has(signTypeNamespacedKey, PersistentDataType.STRING)) {
            String signId = meta.getPersistentDataContainer().get(signTypeNamespacedKey, PersistentDataType.STRING);
            SignInitializer initializer = signInitializerFactory.createInitializer(signId);

            initializer.initializeSign(sign, item);
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        BlockState blockState = event.getBlock().getState();
        if (!(blockState instanceof Sign sign)
                || !sign.getPersistentDataContainer().has(signTypeNamespacedKey, PersistentDataType.STRING)) {
            return;
        }

        event.setCancelled(true);
    }

}
