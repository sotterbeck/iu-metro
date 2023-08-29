package de.sotterbeck.iumetro.entity.ticket;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TicketReaderBalanceTest {

    @Test
    void tap_ShouldDeductCostFromBalance_WhenEntryReader() {
        BigDecimal balance = BigDecimal.valueOf(5);
        BigDecimal usageCost = BigDecimal.valueOf(2.5);
        TicketReader reader = new TicketEntryReader(new SimpleStation("any"));
        RechargeableTicket ticket = new RechargeableTicketBuilder("Rechargeable Ticket",
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
        TicketReader entryReader = new TicketEntryReader(new SimpleStation("any"));
        TicketReader exitReader = new TicketExitReader(new SimpleStation("any"));
        RechargeableTicket ticket = new RechargeableTicketBuilder("Rechargeable Ticket",
                UUID.randomUUID(),
                balance,
                usageCost)
                .build();
        entryReader.tap(ticket);

        exitReader.tap(ticket);
        BigDecimal balanceAfterUse = ticket.balance();

        assertThat(balanceAfterUse).isEqualTo("2.5");
    }

}
