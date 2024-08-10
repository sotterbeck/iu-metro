package de.sotterbeck.iumetro.entrypoint.papermc;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import de.sotterbeck.iumetro.entrypoint.papermc.common.AnnotatedCommand;
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
        Multibinder.newSetBinder(binder(), AnnotatedCommand.class);
        Multibinder.newSetBinder(binder(), Listener.class);

        bind(JavaPlugin.class).toInstance(plugin);
        bind(PluginManager.class).toInstance(plugin.getServer().getPluginManager());
        bind(FileConfiguration.class).toInstance(plugin.getConfig());
    }

}
