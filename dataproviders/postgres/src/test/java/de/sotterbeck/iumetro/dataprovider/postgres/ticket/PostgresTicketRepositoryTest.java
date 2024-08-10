package de.sotterbeck.iumetro.dataprovider.postgres.ticket;

import de.sotterbeck.iumetro.dataprovider.postgres.jooq.generated.Tables;
import de.sotterbeck.iumetro.dataprovider.postgres.jooq.generated.enums.TicketUsageType;
import de.sotterbeck.iumetro.dataprovider.postgres.jooq.generated.tables.records.TicketTimeLimitsRecord;
import de.sotterbeck.iumetro.dataprovider.postgres.jooq.generated.tables.records.TicketUsageLimitsRecord;
import de.sotterbeck.iumetro.dataprovider.postgres.jooq.generated.tables.records.TicketsRecord;
import de.sotterbeck.iumetro.usecase.faregate.UsageDto;
import de.sotterbeck.iumetro.usecase.faregate.UsageType;
import de.sotterbeck.iumetro.usecase.ticket.TicketDto;
import de.sotterbeck.iumetro.usecase.ticket.TicketRepository;
import org.assertj.db.type.Table;
import org.flywaydb.core.Flyway;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.YearToSecond;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.db.api.Assertions.assertThat;

@Testcontainers
class PostgresTicketRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.4-alpine");
    private Table ticketTable;
    private Table ticketTimeLimitTable;
    private Table ticketUsageLimitTable;
    private Table ticketUsagesTable;
    private Table metroStationTable;

    private TicketRepository underTest;
    private DSLContext create;

    @BeforeEach
    void setUp() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(postgres.getJdbcUrl());
        dataSource.setUser(postgres.getUsername());
        dataSource.setPassword(postgres.getPassword());

        ticketTable = new Table(dataSource, "tickets");
        ticketTimeLimitTable = new Table(dataSource, "ticket_time_limits");
        ticketUsageLimitTable = new Table(dataSource, "ticket_usage_limits");
        ticketUsagesTable = new Table(dataSource, "ticket_usages");
        metroStationTable = new Table(dataSource, "metro_stations");

        Flyway flyway = Flyway.configure()
                .dataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())
                .cleanDisabled(false)
                .load();
        flyway.clean();
        flyway.migrate();

        create = DSL.using(dataSource, SQLDialect.POSTGRES);
        underTest = new PostgresTicketRepository(dataSource);
    }

    @Test
    void existsById_ShouldReturnFalse_WhenNoTicketWithIdExists() {
        UUID id = UUID.fromString("d3dca0d1-c93d-44b1-b2e4-79866ac6c217");

        boolean exists = underTest.existsById(id);

        assertThat(exists).isFalse();
    }

    @Test
    void existsById_ShouldReturnTrue_WhenTicketWithIdExists() {
        UUID id = UUID.fromString("bcc48252-68e4-438d-a608-18f134895be3");
        insertTicket(id);

        boolean exists = underTest.existsById(id);

        assertThat(exists).isTrue();
    }

    @Test
    void save_ShouldInsertTicketIntoDatabase_WhenIdDoesNotExist() {
        UUID id = UUID.fromString("855513e4-563f-42bb-b442-8e551434311c");
        TicketDto ticket = new TicketDto(id, "Single-use Ticket");

        underTest.save(ticket);

        assertThat(ticketTable).hasNumberOfRows(1);
    }

    @Test
    void save_ShouldInsertTicketAndTicketLimits_WhenUsageLimits() {
        UUID id = UUID.fromString("855513e4-563f-42bb-b442-8e551434311c");
        TicketDto ticket = new TicketDto(id, "Single-use Ticket", 1, Duration.ofHours(3));

        underTest.save(ticket);

        assertThat(ticketUsageLimitTable).hasNumberOfRows(1);
        assertThat(ticketTimeLimitTable).hasNumberOfRows(1);
    }

    @Test
    void save_ShouldInsertTicketAndTicketLimits_WhenDuplicateUsageLimits() {
        TicketDto ticketOne = new TicketDto(UUID.fromString("855513e4-563f-42bb-b442-8e551434311c"),
                "Single-use Ticket",
                1,
                Duration.ofHours(3));
        TicketDto ticketTwo = new TicketDto(UUID.fromString("8262d47a-3336-4148-a006-dd3044f0e281"),
                "Single-use Ticket 2",
                1,
                Duration.ofHours(3));

        underTest.save(ticketOne);

        Throwable thrown = catchThrowable(() -> underTest.save(ticketTwo));

        assertThat(thrown).isNull();
    }

    @Test
    void get_ShouldGetNoTickets_WhenTicketIdIsNotInDatabase() {
        UUID id = UUID.fromString("855513e4-563f-42bb-b442-8e551434311c");

        Optional<TicketDto> ticket = underTest.get(id);

        assertThat(ticket).isEmpty();
    }

    @Test
    void get_ShouldTicketGetWithUsageLimits_WhenTicketIdIsInDatabase() {
        UUID id = UUID.fromString("855513e4-563f-42bb-b442-8e551434311c");
        insertTicket(id);

        Optional<TicketDto> ticket = underTest.get(id);

        assertThat(ticket).isNotEmpty();
        assertThat(ticket).map(TicketDto::timeLimit).isNotEmpty();
        assertThat(ticket).map(TicketDto::usageLimit).isNotEmpty();
    }

    @Test
    void getAll_ShouldReturnTickets_WhenTicketsInsertedInDatabase() {
        UUID id = UUID.fromString("855513e4-563f-42bb-b442-8e551434311c");
        insertTicket(id);

        List<TicketDto> tickets = underTest.getAll();

        assertThat(tickets).size().isEqualTo(1);

    }

    @Test
    void deleteById_ShouldDeleteTicket_WhenTicketIdIsInDatabase() {
        UUID id = UUID.fromString("855513e4-563f-42bb-b442-8e551434311c");
        insertTicket(id);

        underTest.deleteById(id);

        assertThat(ticketTable).isEmpty();
    }

    @Test
    void getTicketUsages_ShouldThrowException_WhenTicketIdDoesNotExist() {
        UUID id = UUID.fromString("855513e4-563f-42bb-b442-8e551434311c");

        Throwable thrown = catchThrowable(() -> underTest.getTicketUsages(id));

        assertThat(thrown).isNotNull();
    }

    @Test
    void getTicketUsages_ShouldReturnNoUsages_WhenTicketHasNoUsages() {
        UUID id = UUID.fromString("855513e4-563f-42bb-b442-8e551434311c");
        insertTicket(id);

        List<UsageDto> ticketUsages = underTest.getTicketUsages(id);

        assertThat(ticketUsages).isEmpty();
    }

    @Test
    void getTicketUsages_ShouldReturnUsages_WhenTicketHasUsages() {
        UUID ticketId = UUID.fromString("855513e4-563f-42bb-b442-8e551434311c");
        UUID metroStationId = UUID.fromString("9b107ae4-f3b3-4e5d-8e72-9d3149aec77a");
        insertTicket(ticketId);
        insertMetroStation(metroStationId);

        insertTicketUsage(ticketId, metroStationId, TicketUsageType.ENTER);
        List<UsageDto> ticketUsages = underTest.getTicketUsages(ticketId);

        assertThat(ticketUsages).hasSize(1);
    }

    @Test
    void saveTicketUsage_ShouldThrowException_WhenTicketIdDoesNotExist() {
        UUID ticketId = UUID.fromString("855513e4-563f-42bb-b442-8e551434311c");

        Throwable thrown = catchThrowable(() -> underTest.saveTicketUsage(ticketId, new UsageDto("Station",
                ZonedDateTime.now(),
                UsageType.ENTRY)));

        assertThat(thrown).isNotNull();

    }

    @Test
    void saveTicketUsage_ShouldInsertUsage_WhenTicketIdExist() {
        UUID ticketId = UUID.fromString("855513e4-563f-42bb-b442-8e551434311c");
        insertTicket(ticketId);

        underTest.saveTicketUsage(ticketId, new UsageDto("Station", ZonedDateTime.now(), UsageType.ENTRY));

        assertThat(metroStationTable).hasNumberOfRows(1);
        assertThat(ticketUsagesTable).hasNumberOfRows(1);
    }

    private void insertMetroStation(UUID metroStationId) {
        create.newRecord(Tables.METRO_STATIONS)
                .setId(metroStationId)
                .setName("Station")
                .store();
    }

    private void insertTicketUsage(UUID id, UUID metroStationId, TicketUsageType ticketUsageType) {
        create.newRecord(Tables.TICKET_USAGES)
                .setTicketId(id)
                .setMetroStationId(metroStationId)
                .setTimestamp(OffsetDateTime.now())
                .setUsageType(ticketUsageType)
                .store();
    }

    private void insertTicket(UUID id) {
        TicketUsageLimitsRecord usageLimit = create.newRecord(Tables.TICKET_USAGE_LIMITS)
                .setMaxUsages(0);
        int usageLimitId = usageLimit.store();

        TicketTimeLimitsRecord timeLimit = create.newRecord(Tables.TICKET_TIME_LIMITS)
                .setTimeLimit(YearToSecond.valueOf(Duration.ZERO));
        int timeLimitId = timeLimit.store();

        TicketsRecord ticket = create.newRecord(Tables.TICKETS)
                .setId(id)
                .setName("Ticket")
                .setUsageLimitId((long) usageLimitId)
                .setTimeLimitId((long) timeLimitId);
        ticket.store();
    }

}