package de.sotterbeck.iumetro.infra.postgres.network;

import de.sotterbeck.iumetro.app.common.PositionDto;
import de.sotterbeck.iumetro.app.network.graph.MarkerDto;
import de.sotterbeck.iumetro.app.network.graph.StationMarkerRepository;
import de.sotterbeck.iumetro.infra.postgres.jooq.generated.tables.records.MetroStationRailMarkersRecord;
import de.sotterbeck.iumetro.infra.postgres.jooq.generated.tables.records.MetroStationsRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.util.*;

import static de.sotterbeck.iumetro.infra.postgres.jooq.generated.Tables.METRO_STATIONS;
import static de.sotterbeck.iumetro.infra.postgres.jooq.generated.Tables.METRO_STATION_RAIL_MARKERS;

public class PostgresStationMarkerRepository implements StationMarkerRepository {

    public static final RecordMapper<Record, MarkerDto> RECORD_MAPPER = rec -> new MarkerDto(rec.get(METRO_STATIONS.NAME), new PositionDto(
            rec.get(METRO_STATION_RAIL_MARKERS.POS_X),
            rec.get(METRO_STATION_RAIL_MARKERS.POS_Y),
            rec.get(METRO_STATION_RAIL_MARKERS.POS_Z)));

    private final DSLContext create;

    public PostgresStationMarkerRepository(DataSource dataSource) {
        this.create = DSL.using(dataSource, SQLDialect.POSTGRES);
    }

    @Override
    public boolean existsByPosition(PositionDto position) {
        MetroStationRailMarkersRecord marker = create.fetchOne(METRO_STATION_RAIL_MARKERS,
                METRO_STATION_RAIL_MARKERS.POS_X.eq(position.x())
                        .and(METRO_STATION_RAIL_MARKERS.POS_Y.eq(position.y()))
                        .and(METRO_STATION_RAIL_MARKERS.POS_Z.eq(position.z())
                        ));

        return marker != null;
    }

    @Override
    public boolean existsForAllStations() {
        return create.select()
                .from(METRO_STATIONS)
                .leftJoin(METRO_STATION_RAIL_MARKERS).on(METRO_STATIONS.ID.eq(METRO_STATION_RAIL_MARKERS.STATION_ID))
                .where(METRO_STATION_RAIL_MARKERS.STATION_ID.isNull())
                .fetch().isEmpty();
    }

    @Override
    public void save(String stationName, PositionDto position) {
        MetroStationsRecord station = create.fetchOne(METRO_STATIONS, METRO_STATIONS.NAME.eq(stationName));
        if (station == null) {
            throw new IllegalArgumentException("Station not found: " + stationName);
        }

        create.newRecord(METRO_STATION_RAIL_MARKERS)
                .setStationId(station.getId())
                .setPosX(position.x())
                .setPosY(position.y())
                .setPosZ(position.z())
                .store();
    }

    @Override
    public Map<String, List<MarkerDto>> findAll() {
        var groups = create.select(
                        METRO_STATIONS.NAME,
                        METRO_STATION_RAIL_MARKERS.STATION_ID,
                        METRO_STATION_RAIL_MARKERS.POS_X,
                        METRO_STATION_RAIL_MARKERS.POS_Y,
                        METRO_STATION_RAIL_MARKERS.POS_Z
                )
                .from(METRO_STATIONS)
                .leftJoin(METRO_STATION_RAIL_MARKERS).on(METRO_STATIONS.ID.eq(METRO_STATION_RAIL_MARKERS.STATION_ID))
                .orderBy(METRO_STATIONS.NAME.asc())
                .fetchGroups(
                        rec -> rec.get(METRO_STATIONS.NAME),
                        rec -> rec.get(METRO_STATION_RAIL_MARKERS.STATION_ID) != null ? new MarkerDto(
                                rec.get(METRO_STATIONS.NAME),
                                new PositionDto(
                                        rec.get(METRO_STATION_RAIL_MARKERS.POS_X),
                                        rec.get(METRO_STATION_RAIL_MARKERS.POS_Y),
                                        rec.get(METRO_STATION_RAIL_MARKERS.POS_Z)
                                )
                        ) : null
                );

        groups.replaceAll((station, markers) -> markers.stream()
                .filter(Objects::nonNull)
                .toList());

        return groups;
    }

    @Override
    public Collection<MarkerDto> findAllByStation(String stationName) {
        return create.select()
                .from(METRO_STATION_RAIL_MARKERS)
                .leftJoin(METRO_STATIONS).on(METRO_STATIONS.ID.eq(METRO_STATION_RAIL_MARKERS.STATION_ID))
                .where(METRO_STATIONS.NAME.eq(stationName))
                .fetch(RECORD_MAPPER);

    }

    @Override
    public Optional<MarkerDto> findByPosition(PositionDto position) {
        var marker = create.select()
                .from(METRO_STATION_RAIL_MARKERS)
                .leftJoin(METRO_STATIONS).on(METRO_STATIONS.ID.eq(METRO_STATION_RAIL_MARKERS.STATION_ID))
                .where(METRO_STATION_RAIL_MARKERS.POS_X.eq(position.x()))
                .and(METRO_STATION_RAIL_MARKERS.POS_Y.eq(position.y()))
                .and(METRO_STATION_RAIL_MARKERS.POS_Z.eq(position.z()))
                .fetchOne();

        if (marker == null) {
            return Optional.empty();
        }

        return Optional.of(RECORD_MAPPER.apply(marker));
    }

    @Override
    public void deleteByPosition(PositionDto position) {
        var marker = create.fetchOne(METRO_STATION_RAIL_MARKERS,
                METRO_STATION_RAIL_MARKERS.POS_X.eq(position.x())
                        .and(METRO_STATION_RAIL_MARKERS.POS_Y.eq(position.y()))
                        .and(METRO_STATION_RAIL_MARKERS.POS_Z.eq(position.z())
                        ));

        if (marker == null) {
            return;
        }

        marker.delete();
    }

    @Override
    public Map<String, Integer> countMarkersByStation() {
        return create.select()
                .from(METRO_STATIONS)
                .leftJoin(METRO_STATION_RAIL_MARKERS).on(METRO_STATIONS.ID.eq(METRO_STATION_RAIL_MARKERS.STATION_ID))
                .groupBy(METRO_STATIONS.NAME)
                .fetchMap(
                        METRO_STATIONS.NAME,
                        DSL.count()
                );
    }

}
