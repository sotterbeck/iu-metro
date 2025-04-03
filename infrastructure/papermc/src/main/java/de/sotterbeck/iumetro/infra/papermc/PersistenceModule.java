package de.sotterbeck.iumetro.infra.papermc;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import de.sotterbeck.iumetro.app.common.DbMigrator;
import de.sotterbeck.iumetro.infra.papermc.common.FlywayDbMigrator;
import de.sotterbeck.iumetro.infra.papermc.common.HikariDataSourceProvider;
import jakarta.inject.Singleton;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sql.DataSource;

class PersistenceModule extends AbstractModule {

    @Provides
    @Singleton
    static DataSource provideDataSource(FileConfiguration config) {
        String database = config.getString("postgres.database");
        String username = config.getString("postgres.username");
        String password = config.getString("postgres.password");
        return new HikariDataSourceProvider.Builder(database, username, password)
                .host(config.getString("postgres.host"))
                .port(config.getInt("postgres.port"))
                .schema(config.getString("postgres.schema"))
                .build()
                .get();
    }

    @Provides
    @Singleton
    static DbMigrator provideDbMigrator(DataSource dataSource, JavaPlugin plugin) {
        return new FlywayDbMigrator(dataSource, plugin.getClass().getClassLoader());
    }

}
