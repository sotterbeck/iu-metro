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
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static de.sotterbeck.iumetro.infra.postgres.jooq.generated.Tables.*;

public class PostgresMetroNetworkRepository implements MetroNetworkRepository {

    private final DSLContext create;

    private static final RecordMapper<Record, MetroStationDto> MAPPER = new MetroStationDtoRecordMapper();

    public PostgresMetroNetworkRepository(DataSource dataSource) {
        this.create = DSL.using(dataSource, SQLDialect.POSTGRES);
    }

    @Override
    public MetroNetworkDto getEntireNetwork() {
        List<MetroStationDto> metroStations = create.select(
                        METRO_STATIONS.ID,
                        METRO_STATIONS.NAME,
                        METRO_STATION_ALIASES.ALIAS,
                        METRO_STATION_POSITIONS.POS_X,
                        METRO_STATION_POSITIONS.POS_Y,
                        METRO_STATION_POSITIONS.POS_Z,
                        DSL.arrayAgg(METRO_LINES.NAME).filterWhere(METRO_LINES.NAME.isNotNull()).as("lines"),
                        DSL.arrayAgg(METRO_LINES.COLOR).filterWhere(METRO_LINES.COLOR.isNotNull()).as("colors")
                )
                .from(METRO_STATIONS)
                .leftJoin(METRO_STATION_ALIASES).on(METRO_STATIONS.ID.eq(METRO_STATION_ALIASES.METRO_STATION_ID))
                .leftJoin(METRO_STATION_POSITIONS).on(METRO_STATIONS.ID.eq(METRO_STATION_POSITIONS.METRO_STATION_ID))
                .leftJoin(METRO_STATION_LINES).on(METRO_STATIONS.ID.eq(METRO_STATION_LINES.METRO_STATION_ID))
                .leftJoin(METRO_LINES).on(METRO_STATION_LINES.LINE_ID.eq(METRO_LINES.ID))
                .groupBy(
                        METRO_STATIONS.ID,
                        METRO_STATIONS.NAME,
                        METRO_STATION_ALIASES.ALIAS,
                        METRO_STATION_POSITIONS.POS_X,
                        METRO_STATION_POSITIONS.POS_Y,
                        METRO_STATION_POSITIONS.POS_Z
                )
                .fetch()
                .sortAsc(METRO_STATIONS.NAME)
                .map(MAPPER);

        List<ConnectionDto> connections = create.select(
                        METRO_CONNECTIONS.FROM_STATION_ID,
                        METRO_CONNECTIONS.TO_STATION_ID,
                        METRO_CONNECTIONS.DISTANCE
                )
                .from(METRO_CONNECTIONS)
                .groupBy(METRO_CONNECTIONS.FROM_STATION_ID, METRO_CONNECTIONS.TO_STATION_ID, METRO_CONNECTIONS.DISTANCE)
                .fetch(rec -> new ConnectionDto(
                        rec.get(METRO_CONNECTIONS.FROM_STATION_ID, UUID.class),
                        rec.get(METRO_CONNECTIONS.TO_STATION_ID, UUID.class),
                        rec.get(METRO_CONNECTIONS.DISTANCE)
                ));

        return new MetroNetworkDto(metroStations, connections);
    }

    @Override
    public Map<String, StationNodeDto> getGraph() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void saveNetwork(Map<String, StationNodeDto> graph) {
        create.transaction(connection -> {
            var tx = DSL.using(connection);
            tx.deleteFrom(METRO_STATION_LINES).execute();
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
                        METRO_LINES.COLOR
                )
                .from(METRO_LINES)
                .orderBy(METRO_LINES.NAME)
                .fetch(rec -> new LineDto(
                        rec.get(METRO_LINES.NAME),
                        rec.get(METRO_LINES.COLOR)
                ));
    }


}
