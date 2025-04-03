package de.sotterbeck.iumetro.infra.papermc;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.sotterbeck.iumetro.app.common.DbMigrator;
import de.sotterbeck.iumetro.infra.papermc.common.CloudAnnotated;
import de.sotterbeck.iumetro.infra.papermc.common.sign.SignModule;
import de.sotterbeck.iumetro.infra.papermc.common.web.Routing;
import de.sotterbeck.iumetro.infra.papermc.common.web.WebModule;
import de.sotterbeck.iumetro.infra.papermc.faregate.FareGateModule;
import de.sotterbeck.iumetro.infra.papermc.network.MetroNetworkModule;
import de.sotterbeck.iumetro.infra.papermc.station.MetroStationModule;
import de.sotterbeck.iumetro.infra.papermc.ticket.TicketModule;
import io.javalin.Javalin;
import jakarta.inject.Inject;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.meta.CommandMeta;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

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

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Injector injector = Guice.createInjector(
                new PaperPluginModule(this),
                new PersistenceModule(),
                new WebModule(),
                new SignModule(),
                new TicketModule(),
                new FareGateModule(),
                new MetroStationModule(),
                new MetroNetworkModule()
        );

        registerCommands();
        registerEvents();

        migrator.migrate();

        setUpWebServer();

        injector.injectMembers(this);
    }

    private void setUpWebServer() {
        routes.forEach(Routing::bindRoutes);

        javalin.start();
    }

    private void registerEvents() {
        PluginManager pluginManager = getServer().getPluginManager();
        listeners.forEach(listener -> pluginManager.registerEvents(listener, this));
    }

    private void registerCommands() {
        ExecutionCoordinator<CommandSender> executionCoordinator = ExecutionCoordinator.asyncCoordinator();

        LegacyPaperCommandManager<CommandSender> commandManager = new LegacyPaperCommandManager<>(
                this,
                executionCoordinator,
                SenderMapper.identity()
        );

        if (commandManager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            commandManager.registerBrigadier();
        } else if (commandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            commandManager.registerAsynchronousCompletions();
        }

        AnnotationParser<CommandSender> annotationParser = new AnnotationParser<>(
                commandManager,
                CommandSender.class,
                parserParameters -> CommandMeta.empty());

        cloudAnnotated.forEach(annotationParser::parse);
    }

    @Override
    public void onDisable() {
        javalin.stop();
    }

}
