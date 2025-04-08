package de.sotterbeck.iumetro.infra.postgres.network;

import de.sotterbeck.iumetro.app.network.graph.ConnectionDto;
import de.sotterbeck.iumetro.app.network.graph.MetroNetworkDto;
import de.sotterbeck.iumetro.app.network.graph.MetroNetworkRepository;
import de.sotterbeck.iumetro.app.network.graph.StationNodeDto;
import de.sotterbeck.iumetro.app.network.line.LineDto;
import de.sotterbeck.iumetro.app.station.MetroStationDto;
import de.sotterbeck.iumetro.infra.postgres.jooq.generated.tables.records.MetroLinesRecord;
import de.sotterbeck.iumetro.infra.postgres.station.MetroStationDtoRecordMapper;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static de.sotterbeck.iumetro.infra.postgres.jooq.generated.Tables.*;
import static org.jooq.impl.DSL.arrayAgg;
import static org.jooq.impl.DSL.arrayRemove;

public class PostgresMetroNetworkRepository implements MetroNetworkRepository {

    private final DSLContext create;

    private static final RecordMapper<Record, MetroStationDto> METRO_STATION_MAPPER = new MetroStationDtoRecordMapper();

    public PostgresMetroNetworkRepository(DataSource dataSource) {
        this.create = DSL.using(dataSource, SQLDialect.POSTGRES);
    }

    @Override
    public MetroNetworkDto getEntireNetwork() {
        List<MetroStationDto> metroStations = create.select()
                .from(METRO_STATIONS)
                .leftJoin(METRO_STATION_ALIASES).on(METRO_STATIONS.ID.eq(METRO_STATION_ALIASES.METRO_STATION_ID))
                .leftJoin(METRO_STATION_POSITIONS).on(METRO_STATIONS.ID.eq(METRO_STATION_POSITIONS.METRO_STATION_ID))
                .fetch()
                .sortAsc(METRO_STATIONS.NAME)
                .map(METRO_STATION_MAPPER);

        List<ConnectionDto> connections = create.select(
                        METRO_CONNECTIONS.FROM_STATION_ID.as("metro_station1_id"),
                        METRO_CONNECTIONS.TO_STATION_ID.as("metro_station2_id"),
                        arrayRemove(arrayAgg(METRO_LINES.NAME), (String) null).as("line_names")
                )
                .from(METRO_CONNECTIONS)
                .leftJoin(METRO_CONNECTION_LINES).on(METRO_CONNECTIONS.ID.eq(METRO_CONNECTION_LINES.CONNECTION_ID))
                .leftJoin(METRO_LINES).on(METRO_CONNECTION_LINES.LINE_ID.eq(METRO_LINES.ID))
                .groupBy(METRO_CONNECTIONS.FROM_STATION_ID, METRO_CONNECTIONS.TO_STATION_ID)
                .fetch(rec -> new ConnectionDto(
                        rec.get("metro_station1_id", UUID.class),
                        rec.get("metro_station2_id", UUID.class),
                        Arrays.asList(rec.get("line_names", String[].class))
                ));

        List<LineDto> lines = getAllLines();

        return new MetroNetworkDto(metroStations, connections, lines);
    }

    @Override
    public void saveNetwork(Map<String, StationNodeDto> graph) {
        create.transaction(connection -> {
            var tx = DSL.using(connection);
            tx.deleteFrom(METRO_CONNECTION_LINES).execute();
            tx.deleteFrom(METRO_CONNECTIONS).execute();

            var insertSteps = graph.entrySet().stream()
                    .flatMap(entry -> {
                        UUID fromStationId = tx.select(METRO_STATIONS.ID)
                                .from(METRO_STATIONS)
                                .where(METRO_STATIONS.NAME.eq(entry.getKey()))
                                .fetchOne(METRO_STATIONS.ID);

                        return entry.getValue().neighbors().entrySet().stream()
                                .map(neighbor -> tx.insertInto(METRO_CONNECTIONS,
                                                METRO_CONNECTIONS.FROM_STATION_ID,
                                                METRO_CONNECTIONS.TO_STATION_ID,
                                                METRO_CONNECTIONS.DISTANCE)
                                        .values(
                                                fromStationId,
                                                tx.select(METRO_STATIONS.ID)
                                                        .from(METRO_STATIONS)
                                                        .where(METRO_STATIONS.NAME.eq(neighbor.getKey()))
                                                        .fetchOne(METRO_STATIONS.ID),
                                                neighbor.getValue()
                                        ));
                    }).toList();
            tx.batch(insertSteps).execute();
        });
    }

    @Override
    public void saveLine(String name, int color) {
        MetroLinesRecord metroLinesRecord = create.fetchOne(METRO_LINES, METRO_LINES.NAME.eq(name));
        if (metroLinesRecord == null) {
            metroLinesRecord = create.newRecord(METRO_LINES).setName(name);
        }
        assert metroLinesRecord != null;

        metroLinesRecord.setColor(color);
        metroLinesRecord.store();
    }

    @Override
    public void removeLineByName(String name) {
        MetroLinesRecord metroLinesRecord = create.fetchOne(METRO_LINES, METRO_LINES.NAME.eq(name));
        if (metroLinesRecord != null) {
            metroLinesRecord.delete();
        }
    }

    @Override
    public boolean existsLineByName(String name) {
        return create.fetchOne(METRO_LINES, METRO_LINES.NAME.eq(name)) != null;
    }

    @Override
    public List<LineDto> getAllLines() {
        return create.select(
                        METRO_LINES.NAME,
                        METRO_LINES.COLOR,
                        arrayRemove(arrayAgg(METRO_CONNECTIONS.FROM_STATION_ID)
                                .orderBy(METRO_CONNECTION_LINES.SEQUENCE_NUMBER), (UUID) null).as("metro_station_ids")
                )
                .from(METRO_LINES)
                .leftJoin(METRO_CONNECTION_LINES)
                .on(METRO_LINES.ID.eq(METRO_CONNECTION_LINES.LINE_ID))
                .leftJoin(METRO_CONNECTIONS)
                .on(METRO_CONNECTION_LINES.CONNECTION_ID.eq(METRO_CONNECTIONS.ID))
                .groupBy(METRO_LINES.ID, METRO_LINES.NAME, METRO_LINES.COLOR)
                .orderBy(METRO_LINES.NAME)
                .fetch(rec -> new LineDto(
                        rec.get(METRO_LINES.NAME),
                        rec.get(METRO_LINES.COLOR),
                        Arrays.asList(rec.get("metro_station_ids", UUID[].class))
                ));
    }


}
