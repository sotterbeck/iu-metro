package de.sotterbeck.iumetro.entrypoint.papermc;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.sotterbeck.iumetro.entrypoint.papermc.common.AnnotatedCommand;
import de.sotterbeck.iumetro.entrypoint.papermc.common.sign.SignModule;
import de.sotterbeck.iumetro.entrypoint.papermc.faregate.FareGateModule;
import de.sotterbeck.iumetro.entrypoint.papermc.station.MetroStationModule;
import de.sotterbeck.iumetro.entrypoint.papermc.ticket.TicketModule;
import de.sotterbeck.iumetro.usecase.common.DbMigrator;
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
    private Set<AnnotatedCommand> annotatedCommands;

    @Inject
    private Set<Listener> listeners;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Injector injector = Guice.createInjector(
                new PaperPluginModule(this),
                new PersistenceModule(),
                new TicketModule(),
                new SignModule(),
                new FareGateModule(),
                new MetroStationModule()
        );

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

        annotatedCommands.forEach(annotationParser::parse);

        PluginManager pluginManager = getServer().getPluginManager();
        listeners.forEach(listener -> pluginManager.registerEvents(listener, this));

        injector.injectMembers(this);
        migrator.migrate();
    }

}
