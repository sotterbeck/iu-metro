package de.sotterbeck.iumetro.infra.papermc;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import de.sotterbeck.iumetro.app.common.DbMigrator;
import de.sotterbeck.iumetro.infra.papermc.auth.AuthModule;
import de.sotterbeck.iumetro.infra.papermc.auth.LuckPermsModule;
import de.sotterbeck.iumetro.infra.papermc.common.CloudAnnotated;
import de.sotterbeck.iumetro.infra.papermc.common.sign.SignModule;
import de.sotterbeck.iumetro.infra.papermc.common.web.Routing;
import de.sotterbeck.iumetro.infra.papermc.common.web.WebModule;
import de.sotterbeck.iumetro.infra.papermc.faregate.FareGateModule;
import de.sotterbeck.iumetro.infra.papermc.network.MetroNetworkModule;
import de.sotterbeck.iumetro.infra.papermc.retail.RetailTicketModule;
import de.sotterbeck.iumetro.infra.papermc.station.MetroStationModule;
import de.sotterbeck.iumetro.infra.papermc.ticket.TicketModule;
import io.javalin.Javalin;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import jakarta.inject.Inject;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.meta.CommandMeta;
import org.incendo.cloud.paper.PaperCommandManager;

import java.util.ArrayList;
import java.util.Set;

public class IuMetroPlugin extends JavaPlugin {

    @Inject
    private DbMigrator migrator;

    @Inject
    private Javalin javalin;

    @Inject
    private Set<Routing> routes;

    @Inject
    private Set<CloudAnnotated> cloudAnnotated;

    @Inject
    private Set<Listener> listeners;

    @Inject
    private Set<LifecycleListener> lifecycleListeners;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Injector injector;
        try {
            var modules = new ArrayList<Module>();
            modules.add(new PaperPluginModule(this));
            modules.add(new PersistenceModule());
            modules.add(new AuthModule());
            modules.add(new LuckPermsModule());
            modules.add(new WebModule());
            modules.add(new SignModule());
            modules.add(new TicketModule());
            modules.add(new FareGateModule());
            modules.add(new MetroStationModule());
            modules.add(new MetroNetworkModule());
            modules.add(new RetailTicketModule());

            injector = Guice.createInjector(modules);
        } catch (Exception e) {
            getLogger().severe("Failed to create Guice injector: " + e.getMessage());
            throw e;
        }

        injector.injectMembers(this);

        registerCommands();
        registerEvents();
        lifecycleListeners.forEach(LifecycleListener::onEnable);

        migrator.migrate();

        setUpWebServer();
    }

    private void setUpWebServer() {
        routes.forEach(Routing::bindRoutes);
        var port = getConfig().getInt("web.port");

        if (port == 0) {
            getLogger().warning("No port configured for web server. Disabling web server.");
            return;
        }

        javalin.start(port);
    }

    private void registerEvents() {
        PluginManager pluginManager = getServer().getPluginManager();
        listeners.forEach(listener -> pluginManager.registerEvents(listener, this));
    }

    private void registerCommands() {
        ExecutionCoordinator<CommandSourceStack> executionCoordinator = ExecutionCoordinator.asyncCoordinator();

        PaperCommandManager<CommandSourceStack> commandManager = PaperCommandManager.builder()
                .executionCoordinator(executionCoordinator)
                .buildOnEnable(this);

        AnnotationParser<CommandSourceStack> annotationParser = new AnnotationParser<>(
                commandManager,
                CommandSourceStack.class,
                parserParameters -> CommandMeta.empty());

        cloudAnnotated.forEach(annotationParser::parse);
    }

    @Override
    public void onDisable() {
        lifecycleListeners.forEach(LifecycleListener::onDisable);
        if (javalin != null) {
            javalin.stop();
        }
    }

}
