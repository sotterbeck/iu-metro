package de.sotterbeck.iumetro.infra.papermc;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import de.sotterbeck.iumetro.infra.papermc.common.CloudAnnotated;
import de.sotterbeck.iumetro.infra.papermc.common.IuMetroConfig;
import de.sotterbeck.iumetro.infra.papermc.common.SpigotIuMetroConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

class PaperPluginModule extends AbstractModule {

    private final JavaPlugin plugin;

    public PaperPluginModule(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void configure() {
        Multibinder.newSetBinder(binder(), CloudAnnotated.class);
        Multibinder.newSetBinder(binder(), Listener.class);

        bind(JavaPlugin.class).toInstance(plugin);
        bind(PluginManager.class).toInstance(plugin.getServer().getPluginManager());
        bind(FileConfiguration.class).toInstance(plugin.getConfig());
    }

    @Provides
    @Singleton
    static IuMetroConfig provideIuMetroConfig(JavaPlugin plugin) {
        return new SpigotIuMetroConfig(plugin);
    }
}
