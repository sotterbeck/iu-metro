package de.sotterbeck.iumetro.entity.reader;

import de.sotterbeck.iumetro.entity.station.Station;

public interface TicketReaderFactory {

    TicketReader create(ReaderType type, Station station);

    enum ReaderType {
        ENTRY,
        EXIT
    }

}
