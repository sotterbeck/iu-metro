package de.sotterbeck.iumetro.infra.papermc.network;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.ProvidesIntoSet;
import de.sotterbeck.iumetro.app.common.CommonPresenter;
import de.sotterbeck.iumetro.app.network.graph.MetroNetworkGraphService;
import de.sotterbeck.iumetro.app.network.graph.MetroNetworkRepository;
import de.sotterbeck.iumetro.app.network.graph.RailRepository;
import de.sotterbeck.iumetro.app.network.line.LineService;
import de.sotterbeck.iumetro.infra.papermc.common.web.Routing;
import de.sotterbeck.iumetro.infra.postgres.network.PostgresMetroNetworkRepository;
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
    static MetroNetworkGraphService provideMetroNetworkGraphInteractor(MetroNetworkRepository metroNetworkRepository) {
        return new MetroNetworkGraphService(metroNetworkRepository);
    }

    @Provides
    @Singleton
    static CommonPresenter provideLinePresenter() {
        return new WebCommonPresenter();
    }

    @Provides
    @Singleton
    static LineService provideLineManagingInteractor(MetroNetworkRepository metroNetworkRepository, CommonPresenter linePresenter) {
        return new LineService(metroNetworkRepository, linePresenter);
    }

    @Provides
    @Singleton
    static RailRepository provideRailRepository(JavaPlugin plugin) {
        World world = plugin.getServer().getWorlds().getFirst();
        return new SpigotRailRepository(world);
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
