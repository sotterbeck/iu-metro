package de.sotterbeck.iumetro.entrypoint.papermc.station;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.ProvidesIntoSet;
import de.sotterbeck.iumetro.dataprovider.postgres.station.PostgresMetroStationRepository;
import de.sotterbeck.iumetro.entrypoint.papermc.common.AnnotatedCommand;
import de.sotterbeck.iumetro.usecase.station.MetroStationManagingInteractor;
import de.sotterbeck.iumetro.usecase.station.MetroStationRepository;
import jakarta.inject.Singleton;

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

    @ProvidesIntoSet
    static AnnotatedCommand provideMetroStationDeleteCommand(MetroStationManagingInteractor metroStationManagingInteractor) {
        return new MetroStationDeleteCommand(metroStationManagingInteractor);
    }

}
