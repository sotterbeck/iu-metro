package de.sotterbeck.iumetro.infra.postgres.ticket;

import de.sotterbeck.iumetro.app.ticket.TicketConfig;
import de.sotterbeck.iumetro.app.ticket.TicketDto;
import org.jooq.Converter;
import org.jooq.JSONB;
import org.jooq.Record;
import org.jooq.RecordMapper;

import static de.sotterbeck.iumetro.infra.postgres.jooq.generated.Tables.TICKETS;

final class TicketRecordMapper implements RecordMapper<Record, TicketDto> {

    private final Converter<JSONB, TicketConfig> converter;

    TicketRecordMapper(Converter<JSONB, TicketConfig> converter) {
        this.converter = converter;
    }

    @Override
    public TicketDto map(Record rec) {
        return new TicketDto(rec.get(TICKETS.ID), rec.get(TICKETS.NAME), converter.from(rec.get(TICKETS.CONFIG)));
    }

}
