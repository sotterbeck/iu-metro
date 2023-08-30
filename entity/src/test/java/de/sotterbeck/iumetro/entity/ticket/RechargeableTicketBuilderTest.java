package de.sotterbeck.iumetro.entity.ticket;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RechargeableTicketPaidUsageLimitTest {

    @Test
    void tap_ShouldRemoveBalance_WhenPaidUsageLimitIsNotSurpassed() {
        TicketReader entryReader = new TicketEntryReader(new SimpleStation("any"));
        TicketReaderInfo exitReader = new TicketExitReader(new SimpleStation("any"));
        BigDecimal initialBalance = BigDecimal.valueOf(10);
        BigDecimal usageCost = BigDecimal.TWO;

        RechargeableTicket ticket = new RechargeableTicketBuilder("Rechargable Ticket", UUID.randomUUID(), initialBalance, usageCost)
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
        TicketReader entryReader = new TicketEntryReader(new SimpleStation("any"));
        TicketReaderInfo exitReader = new TicketExitReader(new SimpleStation("any"));
        BigDecimal initialBalance = BigDecimal.valueOf(10);
        BigDecimal usageCost = BigDecimal.TWO;

        RechargeableTicket ticket = new RechargeableTicketBuilder("Rechargable Ticket", UUID.randomUUID(), initialBalance, usageCost)
                .paidUsageLimit(1)
                .addUsage(entryReader)
                .addUsage(exitReader)
                .addUsage(entryReader)
                .addUsage(exitReader)
                .build();
        entryReader.tap(ticket);

        assertThat(ticket.balance()).isEqualTo("10");
    }

}