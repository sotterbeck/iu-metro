package de.sotterbeck.iumetro.entrypoint.papermc.common;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;

import java.util.List;

public interface IuMetroConfig {

    List<Component> signLines(String signId);

    Material signMaterial();

}
