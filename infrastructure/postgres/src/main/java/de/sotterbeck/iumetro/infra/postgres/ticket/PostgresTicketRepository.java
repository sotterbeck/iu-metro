package de.sotterbeck.iumetro.infra.postgres.ticket;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sotterbeck.iumetro.app.faregate.UsageDto;
import de.sotterbeck.iumetro.app.faregate.UsageType;
import de.sotterbeck.iumetro.app.ticket.TicketConfig;
import de.sotterbeck.iumetro.app.ticket.TicketDto;
import de.sotterbeck.iumetro.app.ticket.TicketRepository;
import de.sotterbeck.iumetro.infra.postgres.jooq.generated.enums.TicketUsageType;
import de.sotterbeck.iumetro.infra.postgres.jooq.generated.tables.records.MetroStationsRecord;
import de.sotterbeck.iumetro.infra.postgres.jooq.generated.tables.records.TicketUsagesRecord;
import de.sotterbeck.iumetro.infra.postgres.jooq.generated.tables.records.TicketsRecord;
import de.sotterbeck.iumetro.infra.postgres.json.JsonbConverter;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static de.sotterbeck.iumetro.infra.postgres.jooq.generated.Tables.*;

public class PostgresTicketRepository implements TicketRepository {

    private final DSLContext create;

    private final RecordMapper<Record, TicketDto> ticketMapper;
    private final Converter<JSONB, TicketConfig.Config> configConverter;

    public PostgresTicketRepository(DataSource dataSource) {
        create = DSL.using(dataSource, SQLDialect.POSTGRES);

        var objectMapper = new ObjectMapper();
        var type = objectMapper.getTypeFactory()
                .constructType(TicketConfig.Config.class);

        this.configConverter = new JsonbConverter<>(objectMapper, type);
        this.ticketMapper = new TicketRecordMapper(configConverter);
    }

    @Override
    public void save(TicketDto ticket) {
        TicketsRecord ticketsRecord = create.newRecord(TICKETS)
                .setId(ticket.id())
                .setName(ticket.name())
                .setConfig(configConverter.to(ticket.config()));
        ticketsRecord.store();
    }

    @Override
    public Optional<TicketDto> get(UUID id) {
        return create.select(TICKETS.ID, TICKETS.NAME, TICKETS.CONFIG)
                .from(TICKETS)
                .where(TICKETS.ID.eq(id))
                .fetchOptional(ticketMapper)
                .map(PostgresTicketRepository::ensureConfigDefaults);
    }

    @Override
    public List<TicketDto> getAll() {
        return create.select(TICKETS.ID, TICKETS.NAME, TICKETS.CONFIG)
                .from(TICKETS)
                .fetch(ticketMapper)
                .stream()
                .map(PostgresTicketRepository::ensureConfigDefaults)
                .toList();
    }

    private static TicketDto ensureConfigDefaults(TicketDto ticket) {
        if (ticket.config() != null) {
            return ticket;
        }
        return new TicketDto(ticket.id(), ticket.name(), new TicketConfig.Config(List.of()));
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
