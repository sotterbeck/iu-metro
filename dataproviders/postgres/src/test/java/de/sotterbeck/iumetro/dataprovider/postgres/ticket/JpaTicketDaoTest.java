package de.sotterbeck.iumetro.dataprovider.postgres.ticket;

import de.sotterbeck.iumetro.usecase.ticket.TicketDsModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.assertj.db.type.Table;
import org.flywaydb.core.Flyway;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.db.api.Assertions.assertThat;
import static org.hibernate.cfg.AvailableSettings.*;

@Testcontainers
class JpaTicketDaoTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.4-alpine");
    Table ticketTable;
    Table ticketTimeLimitTable;
    Table ticketUsageLimitTable;

    EntityManagerFactory entityManagerFactory;
    JpaTicketDao underTest;

    @BeforeEach
    void setUp() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(postgres.getJdbcUrl());
        dataSource.setUser(postgres.getUsername());
        dataSource.setPassword(postgres.getPassword());

        ticketTable = new Table(dataSource, "tickets");
        ticketTimeLimitTable = new Table(dataSource, "ticket_time_limits");
        ticketUsageLimitTable = new Table(dataSource, "ticket_usage_limits");

        Map<String, Object> properties = new HashMap<>();
        properties.put(URL, postgres.getJdbcUrl());
        properties.put(USER, postgres.getUsername());
        properties.put(PASS, postgres.getPassword());
        properties.put(SHOW_SQL, true);

        entityManagerFactory = Persistence.createEntityManagerFactory("test", properties);

        Flyway flyway = Flyway.configure()
                .dataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())
                .cleanDisabled(false)
                .load();
        flyway.clean();
        flyway.migrate();
        underTest = new JpaTicketDao(entityManagerFactory);
    }

    @Test
    void existsById_ShouldReturnFalse_WhenNoTicketWithIdExists() {
        UUID id = UUID.fromString("d3dca0d1-c93d-44b1-b2e4-79866ac6c217");

        boolean exists = underTest.existsById(id);

        assertThat(exists).isFalse();
    }

    @Test
    void existsById_ShouldReturnTrue_WhenTicketWithIdExists() {
        UUID id = UUID.fromString("d3dca0d1-c93d-44b1-b2e4-79866ac6c217");
        TicketMapper singleUseTicket = createSingleUseTicket(id);

        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            entityManager.persist(singleUseTicket);
            entityManager.getTransaction().commit();
        }

        boolean exists = underTest.existsById(id);

        assertThat(exists).isTrue();
    }

    @Test
    void save_ShouldInsertTicketIntoDatabase_WhenIdDoesNotExist() {
        UUID id = UUID.fromString("855513e4-563f-42bb-b442-8e551434311c");
        TicketDsModel ticket = new TicketDsModel(id, "Single-use Ticket");

        underTest.save(ticket);

        assertThat(ticketTable).hasNumberOfRows(1);
    }

    @Test
    void save_ShouldInsertTicketAndTicketLimits_WhenUsageLimits() {
        UUID id = UUID.fromString("855513e4-563f-42bb-b442-8e551434311c");
        TicketDsModel ticket = new TicketDsModel(id, "Single-use Ticket", 1, Duration.ofHours(3));

        underTest.save(ticket);

        assertThat(ticketUsageLimitTable).hasNumberOfRows(1);
        assertThat(ticketTimeLimitTable).hasNumberOfRows(1);
    }

    @Test
    void save_ShouldInsertTicketAndTicketLimits_WhenDuplicateUsageLimits() {
        TicketDsModel ticketOne = new TicketDsModel(UUID.fromString("855513e4-563f-42bb-b442-8e551434311c"),
                "Single-use Ticket",
                1,
                Duration.ofHours(3));
        TicketDsModel ticketTwo = new TicketDsModel(UUID.fromString("8262d47a-3336-4148-a006-dd3044f0e281"),
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

        Optional<TicketDsModel> ticket = underTest.get(id);

        assertThat(ticket).isEmpty();
    }

    @Test
    void get_ShouldTicketGetWithUsageLimits_WhenTicketIdIsInDatabase() {
        UUID id = UUID.fromString("855513e4-563f-42bb-b442-8e551434311c");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        insertTicketWithTimeAndUsageLimitOf(entityManager, id, new TicketUsageLimitMapper(1), new TicketTimeLimitMapper(Duration.ofHours(3)));

        Optional<TicketDsModel> ticket = underTest.get(id);

        assertThat(ticket).isNotEmpty();
        assertThat(ticket).map(TicketDsModel::timeLimit).isNotEmpty();
        assertThat(ticket).map(TicketDsModel::usageLimit).isNotEmpty();
    }

    @Test
    void getAll_ShouldReturnTickets_WhenTicketsInsertedInDatabase() {
        UUID id = UUID.fromString("855513e4-563f-42bb-b442-8e551434311c");
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            insertTicketWithTimeAndUsageLimitOf(entityManager, id, new TicketUsageLimitMapper(1), new TicketTimeLimitMapper(Duration.ofHours(3)));
        }

        List<TicketDsModel> tickets = underTest.getAll();

        assertThat(tickets).size().isEqualTo(1);

    }

    @Test
    void deleteById_ShouldDeleteTicket_WhenTicketIdIsInDatabase() {
        UUID id = UUID.fromString("855513e4-563f-42bb-b442-8e551434311c");
        TicketUsageLimitMapper usageLimit = new TicketUsageLimitMapper(1);
        TicketTimeLimitMapper timeLimit = new TicketTimeLimitMapper(Duration.ofHours(3));
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            insertTicketWithTimeAndUsageLimitOf(entityManager, id, usageLimit, timeLimit);
        }

        underTest.deleteById(id);

        assertThat(ticketTable).isEmpty();
    }

    @Test
    void deleteById_ShouldDeleteOneTicketAndNoUsageLimits_WhenTicketIdIsInDatabaseAndUsageLimitsAreUsageByMultipleTickets() {
        UUID idOne = UUID.fromString("855513e4-563f-42bb-b442-8e551434311c");
        UUID idTwo = UUID.fromString("111db437-86c0-4def-9f2b-921da274b9a2");
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            TicketUsageLimitMapper usageLimit = new TicketUsageLimitMapper(1);
            TicketTimeLimitMapper timeLimit = new TicketTimeLimitMapper(Duration.ofHours(3));
            insertTicketWithTimeAndUsageLimitOf(entityManager, idOne, usageLimit, timeLimit);
            insertTicketWithTimeAndUsageLimitOf(entityManager, idTwo, usageLimit, timeLimit);
        }

        underTest.deleteById(idOne);

        assertThat(ticketTable).hasNumberOfRows(1);
        assertThat(ticketTimeLimitTable).hasNumberOfRows(1);
        assertThat(ticketUsageLimitTable).hasNumberOfRows(1);
    }

    private void insertTicketWithTimeAndUsageLimitOf(EntityManager entityManager, UUID id, TicketUsageLimitMapper usageLimit, TicketTimeLimitMapper timeLimit) {
        TicketMapper ticketMapper = createSingleUseTicket(id);
        ticketMapper.setUsageLimit(usageLimit);
        ticketMapper.setTimeLimit(timeLimit);

        entityManager.getTransaction().begin();
        entityManager.persist(ticketMapper);
        entityManager.getTransaction().commit();
    }

    @NotNull
    private static TicketMapper createSingleUseTicket(UUID id) {
        return new TicketMapper(id, "Single-use Ticket", new TicketUsageLimitMapper(1), new TicketTimeLimitMapper(Duration.ZERO));
    }

}