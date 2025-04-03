package de.sotterbeck.iumetro.infra.papermc.common;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SpigotIuMetroConfig implements IuMetroConfig {

    private final FileConfiguration configuration;
    private final Logger logger;

    public SpigotIuMetroConfig(Plugin plugin) {
        this.configuration = plugin.getConfig();
        logger = plugin.getLogger();
    }

    @Override
    public List<Component> signLines(String signId) {
        return configuration.getStringList("signs.lines." + signId).stream()
                .map(Components::mm)
                .toList();
    }

    @Override
    public Material signMaterial() {
        String materialString = Objects.requireNonNull(configuration.getString("signs.material")).toUpperCase();

        return Objects.requireNonNullElseGet(Material.getMaterial(materialString), () -> {
            logger.log(Level.WARNING, "Material {0} not found, please check your iuMetro config.", materialString);
            return Material.OAK_SIGN;
        });
    }

}
