package de.sotterbeck.iumetro.dataprovider.postgres.ticket;

import de.sotterbeck.iumetro.usecase.ticket.TicketDsModel;
import org.jooq.Record4;
import org.jooq.RecordMapper;

import static de.sotterbeck.iumetro.dataprovider.postgres.jooq.generated.Tables.*;

public class TicketRecordMapper implements RecordMapper<Record4, TicketDsModel> {

    @Override
    public TicketDsModel map(Record4 record) {
        return new TicketDsModel(record.get(TICKETS.ID), record.get(TICKETS.NAME), record.get(TICKET_USAGE_LIMITS.MAX_USAGES), record.get(TICKET_TIME_LIMITS.TIME_LIMIT).toDuration());
    }

}
