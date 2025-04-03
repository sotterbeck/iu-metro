package de.sotterbeck.iumetro.infra.papermc.faregate;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

public class FareGateKeyFactory {

    private final NamespacedKey fareGateTypeKey;
    private final NamespacedKey stationKey;

    public FareGateKeyFactory(Plugin plugin) {
        fareGateTypeKey = new NamespacedKey(plugin, "fare_gate_type");
        stationKey = new NamespacedKey(plugin, "station");
    }

    public NamespacedKey getFareGateTypeKey() {
        return fareGateTypeKey;
    }

    public NamespacedKey getStationKey() {
        return stationKey;
    }

}
