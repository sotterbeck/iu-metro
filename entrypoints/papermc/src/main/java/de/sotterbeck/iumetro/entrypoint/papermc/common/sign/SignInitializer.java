package de.sotterbeck.iumetro.entrypoint.papermc.common.sign;

import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

public interface SignInitializer {

    void initializeSign(Sign sign, ItemStack item);

}
