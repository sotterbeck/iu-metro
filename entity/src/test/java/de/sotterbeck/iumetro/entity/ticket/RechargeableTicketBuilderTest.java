package de.sotterbeck.iumetro.entity.ticket;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RechargeableTicketPaidUsageLimitTest {

    @Test
    void tap_ShouldRemoveBalance_WhenPaidUsageLimitIsNotSurpassed() {
        TicketReader entryReader = createEntryReader();
        TicketReaderInfo exitReader = createExitReader();
        BigDecimal initialBalance = BigDecimal.valueOf(10);
        BigDecimal usageCost = BigDecimal.TWO;

        RechargeableTicket ticket = Tickets.createRechargeableTicket("Rechargable Ticket", UUID.randomUUID(), initialBalance, usageCost)
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
        BigDecimal usageCost = BigDecimal.TWO;

        RechargeableTicket ticket = Tickets.createRechargeableTicket("Rechargable Ticket", UUID.randomUUID(), initialBalance, usageCost)
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
        return new TicketEntryReaderFactory().create(new SimpleStation("any"));
    }

    private TicketReader createExitReader() {
        return new TicketExitReaderFactory().create(new SimpleStation("any"));
    }

}