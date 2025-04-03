package de.sotterbeck.iumetro.infra.postgres.station;

import de.sotterbeck.iumetro.app.common.PositionDto;
import de.sotterbeck.iumetro.app.station.MetroStationDto;
import org.jooq.Record;
import org.jooq.RecordMapper;

import static de.sotterbeck.iumetro.infra.postgres.jooq.generated.Tables.*;

public class MetroStationDtoRecordMapper implements RecordMapper<Record, MetroStationDto> {

    @Override
    public MetroStationDto map(Record rec) {
        PositionDto position;
        if (rec.get(METRO_STATION_POSITIONS.METRO_STATION_ID) == null) {
            position = null;
        } else {
            position = new PositionDto(
                    rec.get(METRO_STATION_POSITIONS.POS_X),
                    rec.get(METRO_STATION_POSITIONS.POS_Y),
                    rec.get(METRO_STATION_POSITIONS.POS_Z)
            );
        }

        return new MetroStationDto(
                rec.get(METRO_STATIONS.ID),
                rec.get(METRO_STATIONS.NAME),
                rec.get(METRO_STATION_ALIASES.ALIAS),
                position
        );
    }

}
