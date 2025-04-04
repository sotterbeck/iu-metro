package de.sotterbeck.iumetro.infra.papermc.station;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.ProvidesIntoSet;
import de.sotterbeck.iumetro.app.station.MetroStationModificationService;
import de.sotterbeck.iumetro.app.station.MetroStationRepository;
import de.sotterbeck.iumetro.app.station.MetroStationService;
import de.sotterbeck.iumetro.app.station.MetroStationTeleportService;
import de.sotterbeck.iumetro.infra.papermc.common.CloudAnnotated;
import de.sotterbeck.iumetro.infra.papermc.common.web.Routing;
import de.sotterbeck.iumetro.infra.papermc.station.web.MetroStationRouting;
import de.sotterbeck.iumetro.infra.postgres.station.PostgresMetroStationRepository;
import io.javalin.Javalin;
import jakarta.inject.Singleton;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sql.DataSource;

public class MetroStationModule extends AbstractModule {

    @Provides
    @Singleton
    static MetroStationRepository provideMetroStationRepository(DataSource dataSource) {
        return new PostgresMetroStationRepository(dataSource);
    }

    @Provides
    @Singleton
    static MetroStationService provideMetroStationManagingInteractor(MetroStationRepository metroStationRepository) {
        return new MetroStationService(metroStationRepository);
    }

    @Provides
    @Singleton
    static MetroStationModificationService provideMetroStationModifyInteractor(MetroStationRepository metroStationRepository) {
        return new MetroStationModificationService(metroStationRepository);
    }

    @Provides
    @Singleton
    static MetroStationTeleportService provideMetroStationTeleportInteractor(MetroStationRepository metroStationRepository) {
        return new MetroStationTeleportService(metroStationRepository);
    }

    @ProvidesIntoSet
    static CloudAnnotated provideMetroStationListCommand(MetroStationService metroStationService) {
        return new MetroStationListCommand(metroStationService);
    }

    @ProvidesIntoSet
    static CloudAnnotated provideMetroStationDeleteCommand(MetroStationService metroStationService) {
        return new MetroStationDeleteCommand(metroStationService);
    }

    @ProvidesIntoSet
    static CloudAnnotated provideMetroStationModifyCommand(MetroStationService metroStationService, MetroStationModificationService metroStationModificationService) {
        return new MetroStationModifyCommand(metroStationService, metroStationModificationService);
    }

    @ProvidesIntoSet
    static CloudAnnotated provideMetroStationTpCommand(MetroStationTeleportService metroStationTeleportService, JavaPlugin plugin) {
        return new MetroStationTpCommand(metroStationTeleportService, plugin);
    }

    @ProvidesIntoSet
    static Routing provideMetroStationRouting(Javalin javalin, MetroStationService metroStationService) {
        return new MetroStationRouting(javalin, metroStationService);
    }
}
