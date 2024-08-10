package de.sotterbeck.iumetro.entity.ticket;

import de.sotterbeck.iumetro.entity.reader.TicketReader;
import de.sotterbeck.iumetro.entity.reader.TicketReaderFactory;
import de.sotterbeck.iumetro.entity.reader.TicketReaderFactoryImpl;
import de.sotterbeck.iumetro.entity.station.SimpleStation;
import de.sotterbeck.iumetro.entity.ticket.rechargeable.RechargeableTicket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TicketReaderBalanceTest {

    private final TicketReaderFactory ticketReaderFactory = new TicketReaderFactoryImpl();
    TicketFactory ticketFactory;

    @BeforeEach
    void setUp() {
        ticketFactory = new SimpleTicketFactory();
    }

    @Test
    void tap_ShouldDeductCostFromBalance_WhenEntryReader() {
        BigDecimal balance = BigDecimal.valueOf(5);
        BigDecimal usageCost = BigDecimal.valueOf(2.5);
        TicketReader reader = createEntryReader();
        RechargeableTicket ticket = ticketFactory.createRechargeableTicket("Rechargeable Ticket",
                UUID.randomUUID(),
                balance,
                usageCost)
                .build();

        reader.tap(ticket);
        BigDecimal balanceAfterUse = ticket.balance();

        assertThat(balanceAfterUse).isEqualTo("2.5");
    }

    @Test
    void tap_ShouldNotDeductCostFromBalance_WhenExitReader() {
        BigDecimal balance = BigDecimal.valueOf(5);
        BigDecimal usageCost = BigDecimal.valueOf(2.5);
        TicketReader entryReader = createEntryReader();
        TicketReader exitReader = createExitReader();
        RechargeableTicket ticket = ticketFactory.createRechargeableTicket("Rechargeable Ticket",
                UUID.randomUUID(),
                balance,
                usageCost)
                .build();
        entryReader.tap(ticket);

        exitReader.tap(ticket);
        BigDecimal balanceAfterUse = ticket.balance();

        assertThat(balanceAfterUse).isEqualTo("2.5");
    }

    private TicketReader createEntryReader() {
        return ticketReaderFactory.create(TicketReaderFactory.ReaderType.ENTRY, new SimpleStation("any"));
    }

    private TicketReader createExitReader() {
        return ticketReaderFactory.create(TicketReaderFactory.ReaderType.EXIT, new SimpleStation("any"));
    }

}
