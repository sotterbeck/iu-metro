package de.sotterbeck.iumetro.entrypoint.papermc.faregate;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

public class FareGateKeyFactory {

    private final NamespacedKey fareGateTypeKey;
    private final NamespacedKey stationNameKey;

    public FareGateKeyFactory(Plugin plugin) {
        fareGateTypeKey = new NamespacedKey(plugin, "fare_gate_type");
        stationNameKey = new NamespacedKey(plugin, "station_name");
    }

    public NamespacedKey getFareGateTypeKey() {
        return fareGateTypeKey;
    }

    public NamespacedKey getStationNameKey() {
        return stationNameKey;
    }

}
