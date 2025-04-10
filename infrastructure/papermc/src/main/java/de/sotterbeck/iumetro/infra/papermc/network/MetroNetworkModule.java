package de.sotterbeck.iumetro.infra.papermc.network;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.ProvidesIntoSet;
import de.sotterbeck.iumetro.app.common.CommonPresenter;
import de.sotterbeck.iumetro.app.network.graph.*;
import de.sotterbeck.iumetro.app.network.line.LineService;
import de.sotterbeck.iumetro.app.station.MetroStationRepository;
import de.sotterbeck.iumetro.infra.papermc.common.CloudAnnotated;
import de.sotterbeck.iumetro.infra.papermc.common.web.Routing;
import de.sotterbeck.iumetro.infra.postgres.network.PostgresMetroNetworkRepository;
import de.sotterbeck.iumetro.infra.postgres.network.PostgresStationMarkerRepository;
import io.javalin.Javalin;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sql.DataSource;
import java.util.function.Supplier;

public class MetroNetworkModule extends AbstractModule {

    @Provides
    @Singleton
    static MetroNetworkRepository provideMetroNetworkRepository(DataSource dataSource) {
        return new PostgresMetroNetworkRepository(dataSource);
    }

    @Provides
    @Singleton
    static MetroNetworkGraphService provideNetworkGraphService(MetroNetworkRepository metroNetworkRepository) {
        return new MetroNetworkGraphService(metroNetworkRepository);
    }

    @Provides
    @Singleton
    static CommonPresenter provideLinePresenter() {
        return new WebCommonPresenter();
    }

    @Provides
    @Singleton
    static LineService provideLineService(MetroNetworkRepository metroNetworkRepository, CommonPresenter linePresenter) {
        return new LineService(metroNetworkRepository, linePresenter);
    }

    @Provides
    @Singleton
    static StationMarkerService provideStationMarkerService(Supplier<RailRepository> railRepositoryFactory,
                                                            StationMarkerRepository markerRepository,
                                                            MetroStationRepository metroStationRepository,
                                                            MarkerHighlighter highlighter) {
        return new StationMarkerService(railRepositoryFactory, markerRepository, metroStationRepository, highlighter);
    }

    @Provides
    @Singleton
    static StationGraphBuilderService provideStationGraphBuilderService(StationGraphBuilder stationGraphBuilder,
                                                                        StationMarkerRepository markerRepository,
                                                                        MetroNetworkRepository metroNetworkRepository) {
        return new StationGraphBuilderService(stationGraphBuilder, markerRepository, metroNetworkRepository);
    }

    @Provides
    @Singleton
    static StationGraphBuilder provideGraphBuilder(Supplier<RailRepository> railRepositoryFactory) {
        return new BFSStationGraphBuilder(railRepositoryFactory);
    }

    @Provides
    @Singleton
    static Supplier<RailRepository> provideRailRepositoryFactory(JavaPlugin plugin) {
        World world = plugin.getServer().getWorlds().getFirst();
        return () -> new SpigotRailRepository(plugin, world);
    }

    @Provides
    @Singleton
    static StationMarkerRepository provideStationMarkerRepository(DataSource dataSource) {
        return new PostgresStationMarkerRepository(dataSource);
    }

    @Provides
    @Singleton
    static MarkerHighlighter provideMarkerHighlighter(JavaPlugin plugin) {
        var world = plugin.getServer().getWorlds().getFirst();
        return new SpigotMarkerHighlighter(plugin, world);
    }

    @ProvidesIntoSet
    static CloudAnnotated provideMetroStationMarkerCommand(StationMarkerService stationMarkerService) {
        return new MetroStationMarkerCommand(stationMarkerService);
    }

    @ProvidesIntoSet
    static CloudAnnotated provideMetroNetworkBuildCommand(StationGraphBuilderService metroStationService) {
        return new MetroNetworkBuildCommand(metroStationService);
    }

    @ProvidesIntoSet
    static Routing provideMetroNetworkRouting(Javalin javalin, MetroNetworkGraphService metroNetworkGraphService) {
        return new MetroNetworkRouting(javalin, metroNetworkGraphService);
    }

    @ProvidesIntoSet
    static Routing provideLineRouting(Javalin javalin, LineService lineService) {
        return new LineRouting(javalin, lineService);
    }

}
