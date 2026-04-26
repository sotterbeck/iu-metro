package de.sotterbeck.iumetro.infra.papermc.common;

import de.sotterbeck.iumetro.app.auth.SecureTokenGenerator;
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
    private final Plugin plugin;
    private final Logger logger;

    public SpigotIuMetroConfig(Plugin plugin) {
        this.configuration = plugin.getConfig();
        this.plugin = plugin;
        logger = plugin.getLogger();

        initializeAuthSecretKey();
    }

    private void initializeAuthSecretKey() {
        String secretKey = configuration.getString("auth.secretKey", "");
        if (secretKey.isBlank()) {
            String generatedSecret = new SecureTokenGenerator().generateSecureToken();
            configuration.set("auth.secretKey", generatedSecret);
            plugin.saveConfig();
            logger.info("Generated a new auth secret key. Consider setting auth.secretKey explicitly in config.yml for persistence across servers.");
        }
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

    @Override
    public String authSecretKey() {
        return configuration.getString("auth.secretKey", "");
    }

    @Override
    public int authAccessTokenTtlMinutes() {
        return configuration.getInt("auth.accessTokenTtlMinutes", 15);
    }

    @Override
    public int authRefreshTokenTtlDays() {
        return configuration.getInt("auth.refreshTokenTtlDays", 7);
    }

    @Override
    public int authMagicLinkTtlMinutes() {
        return configuration.getInt("auth.magicLinkTtlMinutes", 5);
    }

    @Override
    public String authBaseUrl() {
        return configuration.getString("auth.baseUrl", "");
    }

    @Override
    public List<String> authCorsOrigins() {
        return configuration.getStringList("auth.corsOrigins");
    }

}
