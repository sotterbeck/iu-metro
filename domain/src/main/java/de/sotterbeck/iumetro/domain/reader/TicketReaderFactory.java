package de.sotterbeck.iumetro.domain.reader;

import de.sotterbeck.iumetro.domain.station.Station;

public interface TicketReaderFactory {

    TicketReader create(ReaderType type, Station station);

    enum ReaderType {
        ENTRY,
        EXIT
    }

}
