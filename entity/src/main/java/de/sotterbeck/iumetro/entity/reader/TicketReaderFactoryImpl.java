package de.sotterbeck.iumetro.entity.reader;

import de.sotterbeck.iumetro.entity.station.Station;

public class TicketReaderFactoryImpl implements TicketReaderFactory {

    @Override
    public TicketReader create(ReaderType type, Station station) {
        return switch (type) {
            case ENTRY -> new TicketEntryReader(station);
            case EXIT -> new TicketExitReader(station);
        };
    }

}
