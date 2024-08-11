package de.sotterbeck.iumetro.entrypoint.papermc.faregate.sign;

import de.sotterbeck.iumetro.entrypoint.papermc.common.sign.SignInitializer;
import de.sotterbeck.iumetro.entrypoint.papermc.common.sign.SignTypeKeyFactory;
import de.sotterbeck.iumetro.entrypoint.papermc.faregate.FareGateKeyFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Objects;

public class FareGateSignInitializer implements SignInitializer {

    private final SignTypeKeyFactory signTypeKeyFactory;
    private final FareGateKeyFactory fareGateKeyFactory;
    private final List<Component> lines;

    public FareGateSignInitializer(SignTypeKeyFactory signTypeKeyFactory, FareGateKeyFactory fareGateKeyFactory, List<Component> lines) {
        this.signTypeKeyFactory = signTypeKeyFactory;
        this.fareGateKeyFactory = fareGateKeyFactory;
        this.lines = lines;
    }

    @Override
    public void initializeSign(Sign sign, ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer itemContainer = itemMeta.getPersistentDataContainer();
        PersistentDataContainer signContainer = sign.getPersistentDataContainer();

        NamespacedKey signTypeKey = signTypeKeyFactory.getSignTypeNamespacedKey();
        NamespacedKey fareGateTypeKey = fareGateKeyFactory.getFareGateTypeKey();
        NamespacedKey stationKey = fareGateKeyFactory.getStationKey();

        signContainer.set(signTypeKey, PersistentDataType.STRING, Objects.requireNonNull(itemContainer.get(signTypeKey, PersistentDataType.STRING)));
        signContainer.set(fareGateTypeKey, PersistentDataType.STRING, Objects.requireNonNull(itemContainer.get(fareGateTypeKey, PersistentDataType.STRING)));
        signContainer.set(stationKey, PersistentDataType.STRING, Objects.requireNonNull(itemContainer.get(stationKey, PersistentDataType.STRING)));

        for (int i = 0; i < lines.size(); i++) {
            sign.getSide(Side.FRONT).line(i, lines.get(i));
        }

        sign.update();
    }

}
