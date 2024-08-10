package de.sotterbeck.iumetro.dataprovider.postgres.ticket;

import de.sotterbeck.iumetro.dataprovider.postgres.jooq.generated.enums.TicketUsageType;
import de.sotterbeck.iumetro.dataprovider.postgres.jooq.generated.tables.records.*;
import de.sotterbeck.iumetro.usecase.faregate.UsageDto;
import de.sotterbeck.iumetro.usecase.faregate.UsageType;
import de.sotterbeck.iumetro.usecase.ticket.TicketDto;
import de.sotterbeck.iumetro.usecase.ticket.TicketRepository;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.YearToSecond;

import javax.sql.DataSource;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static de.sotterbeck.iumetro.dataprovider.postgres.jooq.generated.Tables.*;

public class PostgresTicketRepository implements TicketRepository {

    private final DSLContext create;
    private final RecordMapper<Record, TicketDto> ticketMapper = r ->
            new TicketDto(
                    r.get(TICKETS.ID),
                    r.get(TICKETS.NAME),
                    r.get(TICKET_USAGE_LIMITS.MAX_USAGES),
                    r.get(TICKET_TIME_LIMITS.TIME_LIMIT).toDuration());

    public PostgresTicketRepository(DataSource dataSource) {
        create = DSL.using(dataSource, SQLDialect.POSTGRES);

    }

    @Override
    public void save(TicketDto ticket) {
        create.transaction(trx -> {
            DSLContext c = trx.dsl();
            TicketTimeLimitsRecord timeLimitRecord = insertTimeLimit(c, ticket);
            TicketUsageLimitsRecord usageLimitRecord = insertUsageLimit(c, ticket);

            TicketsRecord ticketsRecord = c.newRecord(TICKETS)
                    .setId(ticket.id())
                    .setName(ticket.name())
                    .setUsageLimitId(usageLimitRecord.get(TICKET_USAGE_LIMITS.ID))
                    .setTimeLimitId(timeLimitRecord.get(TICKET_TIME_LIMITS.ID));
            ticketsRecord.store();
        });
    }

    private static TicketUsageLimitsRecord insertUsageLimit(DSLContext dslContext, TicketDto ticket) {
        TicketUsageLimitsRecord usageLimitRecord = dslContext.fetchOne(TICKET_USAGE_LIMITS,
                TICKET_USAGE_LIMITS.MAX_USAGES.eq(ticket.usageLimit()));
        if (usageLimitRecord == null) {
            usageLimitRecord = dslContext.newRecord(TICKET_USAGE_LIMITS)
                    .setMaxUsages(ticket.usageLimit());
            usageLimitRecord.store();
        }
        return usageLimitRecord;
    }

    private static TicketTimeLimitsRecord insertTimeLimit(DSLContext dslContext, TicketDto ticket) {
        TicketTimeLimitsRecord timeLimitRecord = dslContext.fetchOne(TICKET_TIME_LIMITS,
                TICKET_TIME_LIMITS.TIME_LIMIT.eq(YearToSecond.valueOf(ticket.timeLimit())));
        if (timeLimitRecord == null) {
            timeLimitRecord = dslContext.newRecord(TICKET_TIME_LIMITS)
                    .setTimeLimit(YearToSecond.valueOf(ticket.timeLimit()));
            timeLimitRecord.store();
        }
        return timeLimitRecord;
    }

    @Override
    public Optional<TicketDto> get(UUID id) {
        return create.select()
                .from(TICKETS)
                .join(TICKET_USAGE_LIMITS).on(TICKETS.USAGE_LIMIT_ID.eq(TICKET_USAGE_LIMITS.ID))
                .join(TICKET_TIME_LIMITS).on(TICKETS.TIME_LIMIT_ID.eq(TICKET_TIME_LIMITS.ID))
                .where(TICKETS.ID.eq(id))
                .fetchOptional(ticketMapper);
    }

    @Override
    public List<TicketDto> getAll() {
        return create.select()
                .from(TICKETS)
                .join(TICKET_USAGE_LIMITS).on(TICKETS.USAGE_LIMIT_ID.eq(TICKET_USAGE_LIMITS.ID))
                .join(TICKET_TIME_LIMITS).on(TICKETS.TIME_LIMIT_ID.eq(TICKET_TIME_LIMITS.ID))
                .fetch(ticketMapper);
    }

    @Override
    public boolean existsById(UUID id) {
        return create.fetchOne(TICKETS, TICKETS.ID.eq(id)) != null;
    }

    @Override
    public void deleteById(UUID id) {
        TicketsRecord ticket = create.fetchOne(TICKETS, TICKETS.ID.eq(id));
        if (ticket != null) {
            ticket.delete();
        }
    }

    @Override
    public List<UsageDto> getTicketUsages(UUID id) {
        if (!existsById(id)) {
            throw new IllegalArgumentException("Ticket with the id %s does not exist.".formatted(id));
        }
        return create.select()
                .from(TICKET_USAGES.join(METRO_STATIONS)
                        .on(METRO_STATIONS.ID.eq(TICKET_USAGES.METRO_STATION_ID)))
                .where(TICKET_USAGES.TICKET_ID.eq(id))
                .fetch(r -> new UsageDto(r.get(METRO_STATIONS.NAME),
                        r.get(TICKET_USAGES.TIMESTAMP).toZonedDateTime(),
                        fromDbUsageType(r)));
    }

    @Override
    public void saveTicketUsage(UUID ticketId, UsageDto usage) {
        if (!existsById(ticketId)) {
            throw new IllegalArgumentException("Ticket with the id %s does not exist.".formatted(ticketId));
        }
        create.transaction(trx -> {
            DSLContext c = trx.dsl();

            MetroStationsRecord station = c.fetchOne(METRO_STATIONS, METRO_STATIONS.NAME.eq(usage.station()));
            if (station == null) {
                station = c.newRecord(METRO_STATIONS)
                        .setId(UUID.randomUUID())
                        .setName(usage.station());
                station.store();
            }
            TicketUsagesRecord ticketUsage = c.newRecord(TICKET_USAGES)
                    .setMetroStationId(station.getId())
                    .setTicketId(ticketId)
                    .setUsageType(toDbUsageType(usage))
                    .setTimestamp(OffsetDateTime.of(
                            usage.timeAtUsage().toLocalDateTime(),
                            usage.timeAtUsage().getOffset()));
            ticketUsage.store();
        });
    }

    private TicketUsageType toDbUsageType(UsageDto usage) {
        return switch (usage.usageType()) {
            case ENTRY -> TicketUsageType.ENTER;
            case EXIT -> TicketUsageType.EXIT;
        };
    }

    private static UsageType fromDbUsageType(Record usageType) {
        return switch (usageType.get(TICKET_USAGES.USAGE_TYPE)) {
            case ENTER -> UsageType.ENTRY;
            case EXIT -> UsageType.EXIT;
        };
    }

}
