package de.sotterbeck.iumetro.infra.papermc.common.sign;

import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

public interface SignInitializer {

    void initializeSign(Sign sign, ItemStack item);

}
