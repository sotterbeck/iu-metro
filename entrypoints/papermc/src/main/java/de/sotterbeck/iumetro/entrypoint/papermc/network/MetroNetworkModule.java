package de.sotterbeck.iumetro.entrypoint.papermc.network;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.ProvidesIntoSet;
import de.sotterbeck.iumetro.dataprovider.postgres.network.PostgresMetroNetworkRepository;
import de.sotterbeck.iumetro.entrypoint.papermc.common.web.Routing;
import de.sotterbeck.iumetro.usecase.common.CommonPresenter;
import de.sotterbeck.iumetro.usecase.network.graph.MetroNetworkGraphInteractor;
import de.sotterbeck.iumetro.usecase.network.graph.MetroNetworkRepository;
import de.sotterbeck.iumetro.usecase.network.graph.RailRepository;
import de.sotterbeck.iumetro.usecase.network.line.LineConnectionInteractor;
import de.sotterbeck.iumetro.usecase.network.line.LineManagingInteractor;
import de.sotterbeck.iumetro.usecase.station.MetroStationRepository;
import io.javalin.Javalin;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sql.DataSource;

public class MetroNetworkModule extends AbstractModule {

    @Provides
    @Singleton
    static MetroNetworkRepository provideMetroNetworkRepository(DataSource dataSource) {
        return new PostgresMetroNetworkRepository(dataSource);
    }

    @Provides
    @Singleton
    static MetroNetworkGraphInteractor provideMetroNetworkGraphInteractor(MetroNetworkRepository metroNetworkRepository) {
        return new MetroNetworkGraphInteractor(metroNetworkRepository);
    }

    @Provides
    @Singleton
    static CommonPresenter provideLinePresenter() {
        return new WebCommonPresenter();
    }

    @Provides
    @Singleton
    static LineManagingInteractor provideLineManagingInteractor(MetroNetworkRepository metroNetworkRepository, CommonPresenter linePresenter) {
        return new LineManagingInteractor(metroNetworkRepository, linePresenter);
    }

    @Provides
    @Singleton
    static LineConnectionInteractor provideLineConnectionInteractor(MetroNetworkRepository metroNetworkRepository, MetroStationRepository metroStationRepository, CommonPresenter linePresenter) {
        return new LineConnectionInteractor(metroNetworkRepository, metroStationRepository, linePresenter);
    }

    @Provides
    @Singleton
    static RailRepository provideRailRepository(JavaPlugin plugin) {
        World world = plugin.getServer().getWorlds().getFirst();
        return new SpigotRailRepository(world);
    }

    @ProvidesIntoSet
    static Routing provideMetroNetworkRouting(Javalin javalin, MetroNetworkGraphInteractor metroNetworkGraphInteractor, LineConnectionInteractor lineConnectionInteractor) {
        return new MetroNetworkRouting(javalin, metroNetworkGraphInteractor, lineConnectionInteractor);
    }

    @ProvidesIntoSet
    static Routing provideLineRouting(Javalin javalin, LineManagingInteractor lineManagingInteractor) {
        return new LineRouting(javalin, lineManagingInteractor);
    }

}
