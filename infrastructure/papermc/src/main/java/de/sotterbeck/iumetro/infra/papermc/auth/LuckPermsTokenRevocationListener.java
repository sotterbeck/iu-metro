package de.sotterbeck.iumetro.infra.papermc.auth;

import de.sotterbeck.iumetro.app.auth.TokenRevocationService;
import de.sotterbeck.iumetro.infra.papermc.LifecycleListener;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.user.UserDataRecalculateEvent;

public class LuckPermsTokenRevocationListener implements LifecycleListener {

    private final LuckPerms luckPerms;
    private final TokenRevocationService tokenRevocationService;
    private AutoCloseable subscription;

    public LuckPermsTokenRevocationListener(LuckPerms luckPerms, TokenRevocationService tokenRevocationService) {
        this.luckPerms = luckPerms;
        this.tokenRevocationService = tokenRevocationService;
    }

    @Override
    public void onEnable() {
        subscription = luckPerms.getEventBus().subscribe(UserDataRecalculateEvent.class, event -> {
            var user = event.getUser();
            tokenRevocationService.revokeAllForUser(user.getUniqueId());
        });
    }

    @Override
    public void onDisable() {
        if (subscription != null) {
            try {
                subscription.close();
            } catch (Exception ignored) {
            }
            subscription = null;
        }
    }

}
