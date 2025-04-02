package de.sotterbeck.iumetro.dataprovider.postgres.network;

import de.sotterbeck.iumetro.dataprovider.postgres.jooq.generated.tables.records.MetroConnectionsRecord;
import de.sotterbeck.iumetro.dataprovider.postgres.jooq.generated.tables.records.MetroLinesRecord;
import de.sotterbeck.iumetro.dataprovider.postgres.station.MetroStationDtoRecordMapper;
import de.sotterbeck.iumetro.usecase.network.graph.ConnectionDto;
import de.sotterbeck.iumetro.usecase.network.graph.MetroNetworkDto;
import de.sotterbeck.iumetro.usecase.network.graph.MetroNetworkRepository;
import de.sotterbeck.iumetro.usecase.network.line.LineConnectionDto;
import de.sotterbeck.iumetro.usecase.network.line.LineDto;
import de.sotterbeck.iumetro.usecase.station.MetroStationDto;
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

import static de.sotterbeck.iumetro.dataprovider.postgres.jooq.generated.Tables.*;
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

    @Override
    public void saveConnection(UUID station1Id, UUID station2Id, int distance, List<LineConnectionDto> lines) {
        create.transaction(configuration -> {
            DSLContext ctx = DSL.using(configuration);

            Map.Entry<UUID, UUID> sortedIds = sortStationIds(station1Id, station2Id);
            UUID fromStationId = sortedIds.getKey();
            UUID toStationId = sortedIds.getValue();

            MetroConnectionsRecord metroConnectionsRecord = ctx.fetchOne(METRO_CONNECTIONS,
                    METRO_CONNECTIONS.FROM_STATION_ID.eq(fromStationId)
                            .and(METRO_CONNECTIONS.TO_STATION_ID.eq(toStationId)));

            if (metroConnectionsRecord == null) {
                metroConnectionsRecord = ctx.newRecord(METRO_CONNECTIONS)
                        .setFromStationId(fromStationId)
                        .setToStationId(toStationId);
            }

            metroConnectionsRecord
                    .setDistance(distance)
                    .store();

            Long connectionId = metroConnectionsRecord.getId();

            List<String> existingLines = ctx.select(METRO_LINES.NAME)
                    .from(METRO_CONNECTION_LINES)
                    .join(METRO_LINES).on(METRO_CONNECTION_LINES.LINE_ID.eq(METRO_LINES.ID))
                    .where(METRO_CONNECTION_LINES.CONNECTION_ID.eq(connectionId))
                    .fetch(rec -> rec.get(METRO_LINES.NAME));

            List<String> newLineNames = lines.stream()
                    .map(LineConnectionDto::name)
                    .toList();

            List<String> linesToRemove = existingLines.stream()
                    .filter(lineId -> !newLineNames.contains(lineId))
                    .toList();

            if (!linesToRemove.isEmpty()) {
                ctx.deleteFrom(METRO_CONNECTION_LINES)
                        .where(METRO_CONNECTION_LINES.CONNECTION_ID.eq(connectionId))
                        .and(METRO_CONNECTION_LINES.LINE_ID.in(linesToRemove))
                        .execute();
            }

            for (LineConnectionDto line : lines) {
                MetroLinesRecord metroLinesRecord = ctx.fetchOne(METRO_LINES, METRO_LINES.NAME.eq(line.name()));

                if (metroLinesRecord == null) {
                    return;
                }
                long lineId = metroLinesRecord.getId();

                ctx.insertInto(METRO_CONNECTION_LINES)
                        .set(METRO_CONNECTION_LINES.LINE_ID, lineId)
                        .set(METRO_CONNECTION_LINES.CONNECTION_ID, connectionId)
                        .set(METRO_CONNECTION_LINES.SEQUENCE_NUMBER, line.sequenceNumber())
                        .onDuplicateKeyUpdate().set(METRO_CONNECTION_LINES.SEQUENCE_NUMBER, line.sequenceNumber())
                        .execute();
            }
        });
    }

    @Override
    public void removeConnection(UUID station1Id, UUID station2Id) {
        Map.Entry<UUID, UUID> sortedIds = sortStationIds(station1Id, station2Id);
        UUID fromStationId = sortedIds.getKey();
        UUID toStationId = sortedIds.getValue();

        MetroConnectionsRecord metroConnectionsRecord = create.fetchOne(METRO_CONNECTIONS,
                METRO_CONNECTIONS.FROM_STATION_ID.eq(fromStationId)
                        .and(METRO_CONNECTIONS.TO_STATION_ID.eq(toStationId)));

        if (metroConnectionsRecord != null) {
            metroConnectionsRecord.delete();
        }
    }

    @Override
    public boolean existsConnection(UUID station1Id, UUID station2Id) {
        Map.Entry<UUID, UUID> sortedIds = sortStationIds(station1Id, station2Id);
        UUID fromStationId = sortedIds.getKey();
        UUID toStationId = sortedIds.getValue();

        return create.fetchOne(METRO_CONNECTIONS,
                METRO_CONNECTIONS.FROM_STATION_ID.eq(fromStationId)
                        .and(METRO_CONNECTIONS.TO_STATION_ID.eq(toStationId))) != null;
    }

    private Map.Entry<UUID, UUID> sortStationIds(UUID station1Id, UUID station2Id) {
        if (station1Id.compareTo(station2Id) <= 0) {
            return Map.entry(station1Id, station2Id);
        } else {
            return Map.entry(station2Id, station1Id);
        }
    }

}
