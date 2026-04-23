package de.sotterbeck.iumetro.infra.papermc.common;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;

import java.util.List;

// TODO: Replace configuration system with `configurate` library. Currenlty it doesn't update existing config files.
public interface IuMetroConfig {

    List<Component> signLines(String signId);

    Material signMaterial();

    String authSecretKey();

    int authAccessTokenTtlMinutes();

    int authRefreshTokenTtlDays();

    int authMagicLinkTtlMinutes();

    String authBaseUrl();

    List<String> authCorsOrigins();

}
