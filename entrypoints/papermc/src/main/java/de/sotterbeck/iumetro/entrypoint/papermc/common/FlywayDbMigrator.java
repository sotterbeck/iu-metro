package de.sotterbeck.iumetro.entrypoint.papermc.common;

import de.sotterbeck.iumetro.usecase.common.DbMigrator;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;

public class FlywayDbMigrator implements DbMigrator {

    private final DataSource dataSource;
    private final ClassLoader classLoader;

    public FlywayDbMigrator(DataSource dataSource, ClassLoader classLoader) {
        this.dataSource = dataSource;
        this.classLoader = classLoader;
    }

    public void migrate() {
        Flyway flyway = Flyway.configure(this.getClass().getClassLoader())
                .dataSource(this.dataSource)
                .locations("classpath:db/migration")
                .validateMigrationNaming(true)
                .load();
        flyway.migrate();
    }

}