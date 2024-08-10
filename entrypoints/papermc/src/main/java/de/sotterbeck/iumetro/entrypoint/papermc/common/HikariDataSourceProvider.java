package de.sotterbeck.iumetro.entrypoint.papermc.common;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.util.Properties;
import java.util.function.Supplier;

public class HikariDataSourceProvider implements Supplier<DataSource> {

    private final String databaseName;
    private final String user;
    private final String password;
    private final String dataSourceClassName;
    private final String schema;

    private HikariDataSourceProvider(Builder builder) {
        databaseName = builder.databaseName;
        user = builder.user;
        password = builder.password;
        dataSourceClassName = builder.dataSourceClassName;
        schema = builder.schema;
    }

    @Override
    public DataSource get() {
        HikariConfig hikariConfig = new HikariConfig(getDatabaseProperties());
        return new HikariDataSource(hikariConfig);
    }

    private Properties getDatabaseProperties() {
        Properties properties = new Properties();
        properties.setProperty("dataSourceClassName", dataSourceClassName);
        properties.setProperty("dataSource.user", user);
        properties.setProperty("dataSource.password", password);
        properties.setProperty("dataSource.databaseName", databaseName);
        properties.setProperty("dataSource.currentSchema", schema);
        return properties;
    }

    public static class Builder {

        private final String databaseName;
        private final String user;
        private final String password;
        private String dataSourceClassName = "org.postgresql.ds.PGSimpleDataSource";
        private String schema;

        public Builder(String databaseName, String user, String password) {
            this.databaseName = databaseName;
            this.user = user;
            this.password = password;
        }

        public Builder dataSourceClassName(String className) {
            this.dataSourceClassName = className;
            return this;
        }

        public Builder schema(String schema) {
            this.schema = schema;
            return this;
        }

        public Supplier<DataSource> build() {
            return new HikariDataSourceProvider(this);
        }

    }

}