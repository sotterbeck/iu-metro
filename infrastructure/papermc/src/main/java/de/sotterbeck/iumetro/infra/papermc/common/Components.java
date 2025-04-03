package de.sotterbeck.iumetro.infra.papermc.common;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public final class Components {

    private Components() {
    }

    public static Component mm(String miniMessageString) {
        return MiniMessage.miniMessage().deserialize(miniMessageString);
    }

}
