package de.sotterbeck.iumetro.infra.postgres.retail;

import de.sotterbeck.iumetro.app.retail.RetailTicketDto;
import de.sotterbeck.iumetro.app.ticket.TicketConfig;
import org.jooq.Converter;
import org.jooq.JSONB;
import org.jooq.Record;
import org.jooq.RecordMapper;

import static de.sotterbeck.iumetro.infra.postgres.jooq.generated.Tables.RETAIL_TICKETS;
import static de.sotterbeck.iumetro.infra.postgres.jooq.generated.Tables.TICKET_CATEGORIES;

final class RetailTicketDtoRecordMapper implements RecordMapper<Record, RetailTicketDto> {

    private final Converter<JSONB, TicketConfig> converter;

    RetailTicketDtoRecordMapper(Converter<JSONB, TicketConfig> converter) {
        this.converter = converter;
    }

    @Override
    public RetailTicketDto map(Record rec) {
        return new RetailTicketDto(
                rec.get(RETAIL_TICKETS.ID),
                rec.get(RETAIL_TICKETS.NAME),
                rec.get(RETAIL_TICKETS.DESCRIPTION),
                rec.get(RETAIL_TICKETS.PRICE_CENTS),
                converter.from(rec.get(RETAIL_TICKETS.CONFIG)),
                rec.get(RETAIL_TICKETS.IS_ACTIVE),
                rec.get(RETAIL_TICKETS.CREATED_AT).toInstant(),
                rec.get(TICKET_CATEGORIES.NAME)
        );
    }

}
