package de.sotterbeck.iumetro.infra.postgres.retail;

import de.sotterbeck.iumetro.app.retail.RetailTicketDto;
import de.sotterbeck.iumetro.app.retail.RetailTicketRepository;
import de.sotterbeck.iumetro.app.ticket.TicketConfig;
import org.assertj.db.type.AssertDbConnectionFactory;
import org.assertj.db.type.Table;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.db.api.Assertions.assertThat;

@Testcontainers
class PostgresRetailTicketRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.4-alpine");

    private Table retailTicketsTable;
    private Table ticketCategoriesTable;

    private RetailTicketRepository underTest;

    @BeforeEach
    void setUp() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(postgres.getJdbcUrl());
        dataSource.setUser(postgres.getUsername());
        dataSource.setPassword(postgres.getPassword());
        var assertDbConnection = AssertDbConnectionFactory.of(dataSource).create();

        retailTicketsTable = assertDbConnection.table("retail_tickets").build();
        ticketCategoriesTable = assertDbConnection.table("ticket_categories").build();

        Flyway flyway = Flyway.configure()
                .dataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())
                .cleanDisabled(false)
                .load();
        flyway.clean();
        flyway.migrate();

        underTest = new PostgresRetailTicketRepository(dataSource);
    }

    @Test
    void save_ShouldInsertRetailTicket_WhenTicketDoesNotExist() {
        RetailTicketDto ticket = createRetailTicket("75ab8536-43e6-4e7d-b8ea-19d613de1680", "Day Pass");

        underTest.save(ticket);

        assertThat(retailTicketsTable).hasNumberOfRows(1);
        assertThat(ticketCategoriesTable).hasNumberOfRows(1);
    }

    @Test
    void getById_ShouldReturnConstraints_WhenTicketExists() {
        RetailTicketDto ticket = new RetailTicketDto(
                UUID.fromString("bb0f8e5b-1228-4bf8-ae49-813f55e4f2fe"),
                "Weekly Pass",
                "Unlimited rides",
                1999,
                new TicketConfig.Config(List.of(
                        new TicketConfig.UsageLimit(10),
                        new TicketConfig.TimeLimit("PT7D")
                )),
                true,
                Instant.parse("2024-01-01T00:00:00Z"),
                "Weekly"
        );
        underTest.save(ticket);

        Optional<RetailTicketDto> result = underTest.getById(ticket.id());

        assertThat(result).isPresent();
        assertThat(result.get().config().constraints()).containsExactly(
                new TicketConfig.UsageLimit(10),
                new TicketConfig.TimeLimit("PT7D")
        );
    }

    @Test
    void existsByName_ShouldReturnTrue_WhenTicketExists() {
        RetailTicketDto ticket = createRetailTicket("6a8a863b-5c12-4e6c-a2ff-6db168be1ff6", "Monthly Pass");
        underTest.save(ticket);

        boolean exists = underTest.existsByName("Monthly Pass");

        assertThat(exists).isTrue();
    }

    private RetailTicketDto createRetailTicket(String id, String name) {
        return new RetailTicketDto(
                UUID.fromString(id),
                name,
                "Unlimited rides",
                999,
                new TicketConfig.Config(List.of(new TicketConfig.UsageLimit(5))),
                true,
                Instant.parse("2024-01-01T00:00:00Z"),
                "Pass"
        );
    }

}
