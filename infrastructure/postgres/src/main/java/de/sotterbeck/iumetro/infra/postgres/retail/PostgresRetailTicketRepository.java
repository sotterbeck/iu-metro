package de.sotterbeck.iumetro.infra.postgres.retail;

import de.sotterbeck.iumetro.app.retail.RetailTicketDto;
import de.sotterbeck.iumetro.app.retail.RetailTicketRepository;
import de.sotterbeck.iumetro.infra.postgres.jooq.generated.tables.records.RetailTicketsRecord;
import de.sotterbeck.iumetro.infra.postgres.jooq.generated.tables.records.TicketCategoriesRecord;
import de.sotterbeck.iumetro.infra.postgres.jooq.generated.tables.records.TicketTimeLimitsRecord;
import de.sotterbeck.iumetro.infra.postgres.jooq.generated.tables.records.TicketUsageLimitsRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.YearToSecond;

import javax.sql.DataSource;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static de.sotterbeck.iumetro.infra.postgres.jooq.generated.Tables.*;

public class PostgresRetailTicketRepository implements RetailTicketRepository {

    public static final RecordMapper<Record, RetailTicketDto> MAPPER = new RecordRetailTicketDtoRecordMapper();
    private final DSLContext create;

    public PostgresRetailTicketRepository(DataSource dataSource) {
        this.create = DSL.using(dataSource, SQLDialect.POSTGRES);
    }

    @Override
    public Collection<RetailTicketDto> getAll() {
        return create.select()
                .from(RETAIL_TICKETS)
                .join(TICKET_CATEGORIES).on(RETAIL_TICKETS.CATEGORY_ID.eq(TICKET_CATEGORIES.ID))
                .join(TICKET_USAGE_LIMITS).on(RETAIL_TICKETS.USAGE_LIMIT_ID.eq(TICKET_USAGE_LIMITS.ID))
                .join(TICKET_TIME_LIMITS).on(RETAIL_TICKETS.TIME_LIMIT_ID.eq(TICKET_TIME_LIMITS.ID))
                .fetch()
                .map(MAPPER);
    }

    @Override
    public Optional<RetailTicketDto> getById(UUID id) {
        return create.select()
                .from(RETAIL_TICKETS)
                .join(TICKET_CATEGORIES).on(RETAIL_TICKETS.CATEGORY_ID.eq(TICKET_CATEGORIES.ID))
                .join(TICKET_USAGE_LIMITS).on(RETAIL_TICKETS.USAGE_LIMIT_ID.eq(TICKET_USAGE_LIMITS.ID))
                .join(TICKET_TIME_LIMITS).on(RETAIL_TICKETS.TIME_LIMIT_ID.eq(TICKET_TIME_LIMITS.ID))
                .where(RETAIL_TICKETS.ID.eq(id))
                .fetchOptional()
                .map(MAPPER);
    }

    @Override
    public void save(RetailTicketDto ticket) {
        TicketCategoriesRecord ticketCategoriesRecord = Objects.requireNonNullElseGet(
                create.fetchOne(TICKET_CATEGORIES, TICKET_CATEGORIES.NAME.eq(ticket.category())),
                () -> create.newRecord(TICKET_CATEGORIES).setName(ticket.category())
        );

        TicketUsageLimitsRecord ticketUsageLimitsRecord = Objects.requireNonNullElseGet(
                create.fetchOne(TICKET_USAGE_LIMITS, TICKET_USAGE_LIMITS.MAX_USAGES.eq(ticket.usageLimit())),
                () -> create.newRecord(TICKET_USAGE_LIMITS).setMaxUsages(ticket.usageLimit())
        );

        TicketTimeLimitsRecord ticketTimeLimitsRecord = Objects.requireNonNullElseGet(
                create.fetchOne(TICKET_TIME_LIMITS, TICKET_TIME_LIMITS.TIME_LIMIT.eq(YearToSecond.valueOf(ticket.timeLimit()))),
                () -> create.newRecord(TICKET_TIME_LIMITS).setTimeLimit(YearToSecond.valueOf(ticket.timeLimit()))
        );

        RetailTicketsRecord ticketsRecord = Objects.requireNonNullElseGet(
                create.fetchOne(RETAIL_TICKETS, RETAIL_TICKETS.ID.eq(ticket.id())),
                () -> create.newRecord(RETAIL_TICKETS)
                        .setId(ticket.id())
                        .setCreatedAt(OffsetDateTime.now())
        );

        ticketCategoriesRecord.store();
        ticketUsageLimitsRecord.store();
        ticketTimeLimitsRecord.store();

        ticketsRecord
                .setName(ticket.name())
                .setDescription(ticket.description())
                .setPriceCents(ticket.priceCents())
                .setCategoryId(ticketCategoriesRecord.getId())
                .setUsageLimitId(ticketUsageLimitsRecord.getId())
                .setTimeLimitId(ticketTimeLimitsRecord.getId())
                .setIsActive(ticket.isActive())
                .store();
    }

    @Override
    public boolean existsById(UUID id) {
        return create.fetchOne(RETAIL_TICKETS, RETAIL_TICKETS.ID.eq(id)) != null;
    }

    @Override
    public boolean existsByName(String name) {
        return create.fetchOne(RETAIL_TICKETS, RETAIL_TICKETS.NAME.eq(name)) != null;
    }

    @Override
    public void delete(UUID id) {
        RetailTicketsRecord retailTicketsRecord = create.fetchOne(RETAIL_TICKETS, RETAIL_TICKETS.ID.eq(id));

        if (retailTicketsRecord != null) {
            retailTicketsRecord.delete();
        }
    }

    private static class RecordRetailTicketDtoRecordMapper implements RecordMapper<Record, RetailTicketDto> {

        @Override
        public RetailTicketDto map(Record rec) {
            return new RetailTicketDto(
                    rec.get(RETAIL_TICKETS.ID),
                    rec.get(RETAIL_TICKETS.NAME),
                    rec.get(RETAIL_TICKETS.DESCRIPTION),
                    rec.get(RETAIL_TICKETS.PRICE_CENTS),
                    rec.get(TICKET_USAGE_LIMITS.MAX_USAGES),
                    rec.get(TICKET_TIME_LIMITS.TIME_LIMIT).toDuration(),
                    rec.get(RETAIL_TICKETS.IS_ACTIVE),
                    rec.get(RETAIL_TICKETS.CREATED_AT).toInstant(),
                    rec.get(TICKET_CATEGORIES.NAME)
            );
        }

    }

}
