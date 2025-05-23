package de.sotterbeck.iumetro.infra.postgres.station;

import de.sotterbeck.iumetro.app.common.PositionDto;
import de.sotterbeck.iumetro.app.station.MetroStationDto;
import de.sotterbeck.iumetro.app.station.MetroStationRepository;
import de.sotterbeck.iumetro.infra.postgres.jooq.generated.tables.records.MetroStationAliasesRecord;
import de.sotterbeck.iumetro.infra.postgres.jooq.generated.tables.records.MetroStationPositionsRecord;
import de.sotterbeck.iumetro.infra.postgres.jooq.generated.tables.records.MetroStationsRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.util.*;

import static de.sotterbeck.iumetro.infra.postgres.jooq.generated.Tables.*;
import static org.jooq.impl.DSL.inline;

public class PostgresMetroStationRepository implements MetroStationRepository {

    private final DSLContext create;
    private static final RecordMapper<Record, MetroStationDto> MAPPER = new MetroStationDtoRecordMapper();

    public PostgresMetroStationRepository(DataSource dataSource) {
        create = DSL.using(dataSource, SQLDialect.POSTGRES);
    }

    @Override
    public Collection<MetroStationDto> getAll() {
        return create.select()
                .from(METRO_STATIONS)
                .leftJoin(METRO_STATION_ALIASES).on(METRO_STATIONS.ID.eq(METRO_STATION_ALIASES.METRO_STATION_ID))
                .leftJoin(METRO_STATION_POSITIONS).on(METRO_STATIONS.ID.eq(METRO_STATION_POSITIONS.METRO_STATION_ID))
                .fetch()
                .sortAsc(METRO_STATIONS.NAME)
                .map(MAPPER);
    }

    @Override
    public Collection<String> getAllAliases() {
        return create.fetch(METRO_STATION_ALIASES)
                .map(MetroStationAliasesRecord::getAlias);
    }

    @Override
    public Collection<String> getAllStationNames() {
        return create.fetch(METRO_STATIONS)
                .sortAsc(METRO_STATIONS.NAME)
                .map(MetroStationsRecord::getName);
    }

    @Override
    public Optional<MetroStationDto> getByName(String name) {
        return create.select()
                .from(METRO_STATIONS)
                .leftJoin(METRO_STATION_ALIASES).on(METRO_STATIONS.ID.eq(METRO_STATION_ALIASES.METRO_STATION_ID))
                .leftJoin(METRO_STATION_POSITIONS).on(METRO_STATIONS.ID.eq(METRO_STATION_POSITIONS.METRO_STATION_ID))
                .where(METRO_STATIONS.NAME.eq(name))
                .fetchOptional()
                .map(MAPPER);
    }

    @Override
    public Optional<MetroStationDto> getById(UUID id) {
        return create.select()
                .from(METRO_STATIONS)
                .leftJoin(METRO_STATION_ALIASES).on(METRO_STATIONS.ID.eq(METRO_STATION_ALIASES.METRO_STATION_ID))
                .leftJoin(METRO_STATION_POSITIONS).on(METRO_STATIONS.ID.eq(METRO_STATION_POSITIONS.METRO_STATION_ID))
                .where(METRO_STATIONS.ID.eq(id))
                .fetchOptional()
                .map(MAPPER);
    }

    @Override
    public void save(MetroStationDto name) {
        MetroStationsRecord metroStation = create.newRecord(METRO_STATIONS)
                .setId(name.id())
                .setName(name.name());

        metroStation.store();
    }

    @Override
    public void saveAlias(String stationName, String alias) {
        Optional<MetroStationsRecord> metroStation = create.fetchOptional(METRO_STATIONS, METRO_STATIONS.NAME.eq(stationName));

        UUID id = metroStation.map(MetroStationsRecord::getId).orElseThrow(() ->
                new IllegalArgumentException("Station " + stationName + " does not exists"));

        MetroStationAliasesRecord aliasRecord = Objects.requireNonNullElseGet(
                create.fetchOne(METRO_STATION_ALIASES, METRO_STATION_ALIASES.METRO_STATION_ID.eq(id)),
                () -> create.newRecord(METRO_STATION_ALIASES).setMetroStationId(id)
        );

        aliasRecord.setAlias(alias);

        aliasRecord.store();
    }

    @Override
    public void savePosition(String stationName, PositionDto position) {
        Optional<MetroStationsRecord> metroStation = create.fetchOptional(METRO_STATIONS, METRO_STATIONS.NAME.eq(stationName));

        UUID id = metroStation.map(MetroStationsRecord::getId).orElseThrow(() ->
                new IllegalArgumentException("Station " + stationName + " does not exists"));

        MetroStationPositionsRecord positionRecord = Objects.requireNonNullElseGet(
                create.fetchOne(METRO_STATION_POSITIONS, METRO_STATION_POSITIONS.METRO_STATION_ID.eq(id)),
                () -> create.newRecord(METRO_STATION_POSITIONS).setMetroStationId(id)
        );

        positionRecord
                .setPosX(position.x())
                .setPosY(position.y())
                .setPosZ(position.z());

        positionRecord.store();
    }

    @Override
    public void saveLines(String stationName, List<String> lines) {
        create.transaction(configuration -> {
            DSLContext tx = configuration.dsl();

            UUID stationId = tx.select(METRO_STATIONS.ID)
                    .from(METRO_STATIONS)
                    .where(METRO_STATIONS.NAME.eq(stationName))
                    .fetchOne(METRO_STATIONS.ID);

            if (stationId == null) {
                throw new IllegalArgumentException("Station '" + stationName + "' does not exist");
            }

            tx.deleteFrom(METRO_STATION_LINES)
                    .where(METRO_STATION_LINES.METRO_STATION_ID.eq(stationId))
                    .execute();

            if (lines.isEmpty()) {
                return;
            }

            tx.insertInto(
                            METRO_STATION_LINES,
                            METRO_STATION_LINES.METRO_STATION_ID,
                            METRO_STATION_LINES.LINE_ID
                    )
                    .select(
                            tx.select(
                                            inline(stationId),
                                            METRO_LINES.ID
                                    )
                                    .from(METRO_LINES)
                                    .where(METRO_LINES.NAME.in(lines))
                    )
                    .execute();
        });
    }

    @Override
    public boolean existsById(UUID id) {
        return create.fetchOne(METRO_STATIONS, METRO_STATIONS.ID.eq(id)) != null;
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

    @Override
    public void deleteAliasByName(String stationName) {
        Optional<MetroStationsRecord> metroStation = create.fetchOptional(METRO_STATIONS, METRO_STATIONS.NAME.eq(stationName));

        UUID id = metroStation.map(MetroStationsRecord::getId).orElseThrow(() ->
                new IllegalArgumentException("Station " + stationName + " does not exists"));

        MetroStationAliasesRecord aliasRecord = create.fetchOne(METRO_STATION_ALIASES, METRO_STATION_ALIASES.METRO_STATION_ID.eq(id));
        if (aliasRecord != null) {
            aliasRecord.delete();
        }
    }

    @Override
    public void deletePositionByName(String stationName) {
        Optional<MetroStationsRecord> metroStation = create.fetchOptional(METRO_STATIONS, METRO_STATIONS.NAME.eq(stationName));

        UUID id = metroStation.map(MetroStationsRecord::getId).orElseThrow(() ->
                new IllegalArgumentException("Station " + stationName + " does not exists"));

        MetroStationPositionsRecord positionRecord = create.fetchOne(METRO_STATION_POSITIONS, METRO_STATION_POSITIONS.METRO_STATION_ID.eq(id));
        if (positionRecord != null) {
            positionRecord.delete();
        }
    }

}
