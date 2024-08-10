package de.sotterbeck.iumetro.entrypoint.papermc.common.sign;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

public class SignTypeKeyFactory {

    private final NamespacedKey signType;

    public SignTypeKeyFactory(Plugin plugin) {
        signType = new NamespacedKey(plugin, "sign_type");
    }

    public NamespacedKey getSignTypeNamespacedKey() {
        return signType;
    }

}
