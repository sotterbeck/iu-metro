package de.sotterbeck.iumetro.infra.postgres.station;

import de.sotterbeck.iumetro.app.common.PositionDto;
import de.sotterbeck.iumetro.app.network.line.LineDto;
import de.sotterbeck.iumetro.app.station.MetroStationDto;
import org.jooq.Record;
import org.jooq.RecordMapper;

import java.util.ArrayList;
import java.util.List;

import static de.sotterbeck.iumetro.infra.postgres.jooq.generated.Tables.*;

/**
 * Maps a jOOQ Record to a {@link MetroStationDto}.
 * <p>
 * This class is used to map the result of a jOOQ query to a {@link MetroStationDto} object.
 * to convert the result of a query to a {@link MetroStationDto} object.
 * <p>
 * To map lines, it expects the two extra columns <code>lines</code> and <code>colors</code> to be present in the query result.
 * If these columns are not present, the lines will be empty.
 */
public class MetroStationDtoRecordMapper implements RecordMapper<Record, MetroStationDto> {

    @Override
    public MetroStationDto map(Record rec) {
        var position = getPos(rec);
        var lines = getLines(rec);

        return new MetroStationDto(
                rec.get(METRO_STATIONS.ID),
                rec.get(METRO_STATIONS.NAME),
                rec.get(METRO_STATION_ALIASES.ALIAS),
                position,
                lines
        );
    }

    private List<LineDto> getLines(Record rec) {
        var lineRecs = rec.get("lines", String[].class);
        var colorRecs = rec.get("colors", Integer[].class);
        if (lineRecs == null || colorRecs == null) {
            return List.of();
        }

        List<LineDto> lines = new ArrayList<>();
        for (int i = 0; i < lineRecs.length; i++) {
            lines.add(new LineDto(lineRecs[i], colorRecs[i]));
        }

        return lines;
    }

    private PositionDto getPos(Record rec) {
        Integer posX = rec.get(METRO_STATION_POSITIONS.POS_X);
        Integer posY = rec.get(METRO_STATION_POSITIONS.POS_Y);
        Integer posZ = rec.get(METRO_STATION_POSITIONS.POS_Z);

        return (posX != null && posY != null && posZ != null)
                ? new PositionDto(posX, posY, posZ)
                : null;
    }

}
