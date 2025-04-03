package de.sotterbeck.iumetro.infra.postgres.ticket;

import de.sotterbeck.iumetro.app.ticket.TicketDto;
import org.jooq.Record4;
import org.jooq.RecordMapper;

import static de.sotterbeck.iumetro.infra.postgres.jooq.generated.Tables.*;

public class TicketRecordMapper implements RecordMapper<Record4, TicketDto> {

    @Override
    public TicketDto map(Record4 record) {
        return new TicketDto(record.get(TICKETS.ID), record.get(TICKETS.NAME), record.get(TICKET_USAGE_LIMITS.MAX_USAGES), record.get(TICKET_TIME_LIMITS.TIME_LIMIT).toDuration());
    }

}
