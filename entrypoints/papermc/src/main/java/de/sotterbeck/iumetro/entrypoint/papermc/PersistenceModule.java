package de.sotterbeck.iumetro.entrypoint.papermc;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import de.sotterbeck.iumetro.entrypoint.papermc.common.FlywayDbMigrator;
import de.sotterbeck.iumetro.entrypoint.papermc.common.HikariDataSourceProvider;
import de.sotterbeck.iumetro.usecase.common.DbMigrator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sql.DataSource;

class PersistenceModule extends AbstractModule {

    @Provides
    static DataSource provideDataSource(FileConfiguration config) {
        return new HikariDataSourceProvider.Builder(
                config.getString("postgres.database"),
                config.getString("postgres.username"),
                config.getString("postgres.password"))
                .schema(config.getString("postgres.schema"))
                .build()
                .get();
    }

    @Provides
    static DbMigrator provideDbMigrator(DataSource dataSource, JavaPlugin plugin) {
        return new FlywayDbMigrator(dataSource, plugin.getClass().getClassLoader());
    }

}
