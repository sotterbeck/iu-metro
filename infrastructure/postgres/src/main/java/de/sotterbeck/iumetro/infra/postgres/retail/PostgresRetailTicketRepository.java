package de.sotterbeck.iumetro.infra.postgres.retail;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sotterbeck.iumetro.app.retail.RetailTicketDto;
import de.sotterbeck.iumetro.app.retail.RetailTicketRepository;
import de.sotterbeck.iumetro.app.ticket.TicketConfig;
import de.sotterbeck.iumetro.infra.postgres.jooq.generated.tables.records.RetailTicketsRecord;
import de.sotterbeck.iumetro.infra.postgres.jooq.generated.tables.records.TicketCategoriesRecord;
import de.sotterbeck.iumetro.infra.postgres.json.JsonbConverter;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static de.sotterbeck.iumetro.infra.postgres.jooq.generated.Tables.RETAIL_TICKETS;
import static de.sotterbeck.iumetro.infra.postgres.jooq.generated.Tables.TICKET_CATEGORIES;

public class PostgresRetailTicketRepository implements RetailTicketRepository {

    public final RecordMapper<Record, RetailTicketDto> mapper;
    private final DSLContext create;
    private final Converter<JSONB, TicketConfig> configConverter;

    public PostgresRetailTicketRepository(DataSource dataSource) {
        this.create = DSL.using(dataSource, SQLDialect.POSTGRES);
        var objectMapper = new ObjectMapper();
        var type = objectMapper.getTypeFactory()
                .constructType(TicketConfig.class);

        this.configConverter = new JsonbConverter<>(objectMapper, type);
        this.mapper = new RetailTicketDtoRecordMapper(configConverter);
    }

    @Override
    public Collection<RetailTicketDto> getAll() {
        return create.select(RETAIL_TICKETS.ID,
                        RETAIL_TICKETS.NAME,
                        RETAIL_TICKETS.DESCRIPTION,
                        RETAIL_TICKETS.PRICE_CENTS,
                        RETAIL_TICKETS.CONFIG,
                        RETAIL_TICKETS.IS_ACTIVE,
                        RETAIL_TICKETS.CREATED_AT,
                        TICKET_CATEGORIES.NAME)
                .from(RETAIL_TICKETS)
                .join(TICKET_CATEGORIES).on(RETAIL_TICKETS.CATEGORY_ID.eq(TICKET_CATEGORIES.ID))
                .fetch()
                .map(mapper)
                .stream()
                .map(PostgresRetailTicketRepository::ensureConfigDefaults)
                .toList();
    }

    @Override
    public Optional<RetailTicketDto> getById(UUID id) {
        return create.select(RETAIL_TICKETS.ID,
                        RETAIL_TICKETS.NAME,
                        RETAIL_TICKETS.DESCRIPTION,
                        RETAIL_TICKETS.PRICE_CENTS,
                        RETAIL_TICKETS.CONFIG,
                        RETAIL_TICKETS.IS_ACTIVE,
                        RETAIL_TICKETS.CREATED_AT,
                        TICKET_CATEGORIES.NAME)
                .from(RETAIL_TICKETS)
                .join(TICKET_CATEGORIES).on(RETAIL_TICKETS.CATEGORY_ID.eq(TICKET_CATEGORIES.ID))
                .where(RETAIL_TICKETS.ID.eq(id))
                .fetchOptional()
                .map(mapper)
                .map(PostgresRetailTicketRepository::ensureConfigDefaults);
    }

    @Override
    public Map<String, Collection<RetailTicketDto>> getAllGroupedByCategory() {
        return create.select(RETAIL_TICKETS.ID,
                        RETAIL_TICKETS.NAME,
                        RETAIL_TICKETS.DESCRIPTION,
                        RETAIL_TICKETS.PRICE_CENTS,
                        RETAIL_TICKETS.CONFIG,
                        RETAIL_TICKETS.IS_ACTIVE,
                        RETAIL_TICKETS.CREATED_AT,
                        TICKET_CATEGORIES.NAME)
                .from(RETAIL_TICKETS)
                .join(TICKET_CATEGORIES).on(RETAIL_TICKETS.CATEGORY_ID.eq(TICKET_CATEGORIES.ID))
                .fetch()
                .map(mapper)
                .stream()
                .map(PostgresRetailTicketRepository::ensureConfigDefaults)
                .collect(Collectors.groupingBy(
                        RetailTicketDto::category,
                        java.util.HashMap::new,
                        Collectors.toCollection(java.util.ArrayList::new)));
    }

    @Override
    public void save(RetailTicketDto ticket) {
        TicketCategoriesRecord ticketCategoriesRecord = Objects.requireNonNullElseGet(
                create.fetchOne(TICKET_CATEGORIES, TICKET_CATEGORIES.NAME.eq(ticket.category())),
                () -> create.newRecord(TICKET_CATEGORIES).setName(ticket.category())
        );

        RetailTicketsRecord ticketsRecord = Objects.requireNonNullElseGet(
                create.fetchOne(RETAIL_TICKETS, RETAIL_TICKETS.ID.eq(ticket.id())),
                () -> create.newRecord(RETAIL_TICKETS)
                        .setId(ticket.id())
                        .setCreatedAt(OffsetDateTime.now())
        );

        ticketCategoriesRecord.store();

        ticketsRecord
                .setName(ticket.name())
                .setDescription(ticket.description())
                .setPriceCents(ticket.priceCents())
                .setCategoryId(ticketCategoriesRecord.getId())
                .setConfig(configConverter.to(ticket.config()))
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

    private static RetailTicketDto ensureConfigDefaults(RetailTicketDto ticket) {
        if (ticket.config() != null) {
            return ticket;
        }
        return new RetailTicketDto(ticket.id(), ticket.name(), ticket.description(), ticket.priceCents(), new TicketConfig(List.of()),
                ticket.isActive(), ticket.createdAt(), ticket.category());
    }

}
