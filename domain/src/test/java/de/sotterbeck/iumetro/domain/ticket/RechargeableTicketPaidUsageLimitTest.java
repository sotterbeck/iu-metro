package de.sotterbeck.iumetro.domain.ticket;

import de.sotterbeck.iumetro.domain.reader.TicketReader;
import de.sotterbeck.iumetro.domain.reader.TicketReaderFactory;
import de.sotterbeck.iumetro.domain.reader.TicketReaderFactoryImpl;
import de.sotterbeck.iumetro.domain.reader.TicketReaderInfo;
import de.sotterbeck.iumetro.domain.station.SimpleStation;
import de.sotterbeck.iumetro.domain.ticket.rechargeable.RechargeableTicket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RechargeableTicketPaidUsageLimitTest {

    private final TicketReaderFactory ticketReaderFactory = new TicketReaderFactoryImpl();
    private TicketFactory ticketFactory;

    @BeforeEach
    void setUp() {
        ticketFactory = new SimpleTicketFactory();
    }

    @Test
    void tap_ShouldRemoveBalance_WhenPaidUsageLimitIsNotSurpassed() {
        TicketReader entryReader = createEntryReader();
        TicketReaderInfo exitReader = createExitReader();
        BigDecimal initialBalance = BigDecimal.valueOf(10);
        BigDecimal usageCost = BigDecimal.valueOf(2);

        RechargeableTicket ticket = ticketFactory.createRechargeableTicket("Rechargable Ticket", UUID.randomUUID(), initialBalance, usageCost)
                .paidUsageLimit(10)
                .addUsage(entryReader)
                .addUsage(exitReader)
                .addUsage(entryReader)
                .addUsage(exitReader)
                .build();
        entryReader.tap(ticket);

        assertThat(ticket.balance()).isEqualTo("8");
    }

    @Test
    void tap_ShouldNotRemoveBalance_WhenPaidUsageLimitIsSurpassed() {
        TicketReader entryReader = createEntryReader();
        TicketReaderInfo exitReader = createExitReader();
        BigDecimal initialBalance = BigDecimal.valueOf(10);
        BigDecimal usageCost = BigDecimal.valueOf(2);

        RechargeableTicket ticket = ticketFactory.createRechargeableTicket("Rechargable Ticket", UUID.randomUUID(), initialBalance, usageCost)
                .paidUsageLimit(1)
                .addUsage(entryReader)
                .addUsage(exitReader)
                .addUsage(entryReader)
                .addUsage(exitReader)
                .build();
        entryReader.tap(ticket);

        assertThat(ticket.balance()).isEqualTo("10");
    }

    private TicketReader createEntryReader() {
        return ticketReaderFactory.create(TicketReaderFactory.ReaderType.ENTRY, new SimpleStation("any"));
    }

    private TicketReader createExitReader() {
        return ticketReaderFactory.create(TicketReaderFactory.ReaderType.EXIT, new SimpleStation("any"));
    }

}