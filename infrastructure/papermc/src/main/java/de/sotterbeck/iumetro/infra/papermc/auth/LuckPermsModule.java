package de.sotterbeck.iumetro.infra.papermc.auth;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import de.sotterbeck.iumetro.infra.papermc.LifecycleListener;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;

public class LuckPermsModule extends AbstractModule {

    @Override
    protected void configure() {
        Multibinder<LifecycleListener> multibinder = Multibinder.newSetBinder(binder(), LifecycleListener.class);
        if (Bukkit.getPluginManager().getPlugin("LuckPerms") == null) {
            return;
        }

        bind(LuckPerms.class).toInstance(LuckPermsProvider.get());

        multibinder.addBinding().to(LuckPermsTokenRevocationListener.class);
    }

}
