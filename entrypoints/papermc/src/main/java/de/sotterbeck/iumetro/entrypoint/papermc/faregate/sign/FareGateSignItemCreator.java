package de.sotterbeck.iumetro.entrypoint.papermc.faregate.sign;

import de.sotterbeck.iumetro.entrypoint.papermc.common.Components;
import de.sotterbeck.iumetro.entrypoint.papermc.common.IuMetroConfig;
import de.sotterbeck.iumetro.entrypoint.papermc.common.sign.SignTypeKeyFactory;
import de.sotterbeck.iumetro.entrypoint.papermc.faregate.FareGateKeyFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class FareGateSignItemCreator {

    private final SignTypeKeyFactory signTypeKeyFactory;
    private final FareGateKeyFactory fareGateKeyFactory;
    private final IuMetroConfig config;

    public FareGateSignItemCreator(SignTypeKeyFactory signTypeKeyFactory, FareGateKeyFactory fareGateKeyFactory, IuMetroConfig config) {
        this.signTypeKeyFactory = signTypeKeyFactory;
        this.fareGateKeyFactory = fareGateKeyFactory;
        this.config = config;
    }

    public ItemStack createItem(String type, String stationName, String stationId, String stationDisplayId) {
        Material defaultMaterial = config.signMaterial();
        ItemStack item = new ItemStack(defaultMaterial);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Components.mm("<gold>" + stationName + " Fare Gate Sign"));
        List<Component> lore = List.of(
                Components.mm("<gray>Station: " + stationName + " (" + stationDisplayId + ")"),
                Components.mm("<gray>Type: " + type)
        );
        meta.lore(lore);

        NamespacedKey signTypeKey = signTypeKeyFactory.getSignTypeNamespacedKey();
        NamespacedKey fareGateTypeKey = fareGateKeyFactory.getFareGateTypeKey();
        NamespacedKey stationKey = fareGateKeyFactory.getStationKey();

        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(signTypeKey, PersistentDataType.STRING, "faregate_" + type.toLowerCase());
        container.set(fareGateTypeKey, PersistentDataType.STRING, type);
        container.set(stationKey, PersistentDataType.STRING, stationId);

        item.setItemMeta(meta);
        return item;
    }

}
