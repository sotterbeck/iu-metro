package de.sotterbeck.iumetro.infra.papermc.station;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.ProvidesIntoSet;
import de.sotterbeck.iumetro.app.station.MetroStationManagingInteractor;
import de.sotterbeck.iumetro.app.station.MetroStationModifyInteractor;
import de.sotterbeck.iumetro.app.station.MetroStationRepository;
import de.sotterbeck.iumetro.app.station.MetroStationTeleportInteractor;
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
    static MetroStationManagingInteractor provideMetroStationManagingInteractor(MetroStationRepository metroStationRepository) {
        return new MetroStationManagingInteractor(metroStationRepository);
    }

    @Provides
    @Singleton
    static MetroStationModifyInteractor provideMetroStationModifyInteractor(MetroStationRepository metroStationRepository) {
        return new MetroStationModifyInteractor(metroStationRepository);
    }

    @Provides
    @Singleton
    static MetroStationTeleportInteractor provideMetroStationTeleportInteractor(MetroStationRepository metroStationRepository) {
        return new MetroStationTeleportInteractor(metroStationRepository);
    }

    @ProvidesIntoSet
    static CloudAnnotated provideMetroStationListCommand(MetroStationManagingInteractor metroStationManagingInteractor) {
        return new MetroStationListCommand(metroStationManagingInteractor);
    }

    @ProvidesIntoSet
    static CloudAnnotated provideMetroStationDeleteCommand(MetroStationManagingInteractor metroStationManagingInteractor) {
        return new MetroStationDeleteCommand(metroStationManagingInteractor);
    }

    @ProvidesIntoSet
    static CloudAnnotated provideMetroStationModifyCommand(MetroStationManagingInteractor metroStationManagingInteractor, MetroStationModifyInteractor metroStationModifyInteractor) {
        return new MetroStationModifyCommand(metroStationManagingInteractor, metroStationModifyInteractor);
    }

    @ProvidesIntoSet
    static CloudAnnotated provideMetroStationTpCommand(MetroStationTeleportInteractor metroStationTeleportInteractor, JavaPlugin plugin) {
        return new MetroStationTpCommand(metroStationTeleportInteractor, plugin);
    }

    @ProvidesIntoSet
    static Routing provideMetroStationRouting(Javalin javalin, MetroStationManagingInteractor metroStationManagingInteractor) {
        return new MetroStationRouting(javalin, metroStationManagingInteractor);
    }
}
