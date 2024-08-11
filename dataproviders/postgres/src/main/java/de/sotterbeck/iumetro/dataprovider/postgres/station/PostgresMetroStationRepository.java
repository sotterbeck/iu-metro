package de.sotterbeck.iumetro.dataprovider.postgres.station;

import de.sotterbeck.iumetro.dataprovider.postgres.jooq.generated.tables.records.MetroStationsRecord;
import de.sotterbeck.iumetro.usecase.station.MetroStationDto;
import de.sotterbeck.iumetro.usecase.station.MetroStationRepository;
import org.jooq.DSLContext;
import org.jooq.RecordMapper;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Optional;

import static de.sotterbeck.iumetro.dataprovider.postgres.jooq.generated.Tables.METRO_STATIONS;

public class PostgresMetroStationRepository implements MetroStationRepository {

    private final DSLContext create;
    private final RecordMapper<? super MetroStationsRecord, MetroStationDto> metroStationMapper = r -> new MetroStationDto(
            r.getId(),
            r.getName()
    );

    public PostgresMetroStationRepository(DataSource dataSource) {
        create = DSL.using(dataSource, SQLDialect.POSTGRES);
    }

    @Override
    public Collection<MetroStationDto> getAll() {
        Result<MetroStationsRecord> result = create.fetch(METRO_STATIONS);
        return result.map(metroStationMapper);
    }

    @Override
    public Optional<MetroStationDto> getByName(String name) {
        return create.fetchOptional(METRO_STATIONS, METRO_STATIONS.NAME.eq(name)).map(metroStationMapper);
    }

    @Override
    public void save(MetroStationDto station) {
        MetroStationsRecord metroStation = create.newRecord(METRO_STATIONS)
                .setId(station.id())
                .setName(station.name());

        metroStation.store();
    }

    @Override
    public boolean existsByName(String name) {
        return create.fetchOne(METRO_STATIONS, METRO_STATIONS.NAME.eq(name)) != null;
    }

    @Override
    public void deleteByName(String name) {
        MetroStationsRecord metroStation = create.fetchOne(METRO_STATIONS, METRO_STATIONS.NAME.eq(name));

        if (metroStation != null) {
            metroStation.delete();
        }
    }

}
