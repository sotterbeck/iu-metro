package de.sotterbeck.iumetro.domain.ticket;

import de.sotterbeck.iumetro.domain.ticket.rechargeable.RechargeableTicket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RechargeableTicketValidationTest {

    private TicketFactory ticketFactory;

    @BeforeEach
    void setUp() {
        ticketFactory = new SimpleTicketFactory();
    }

    @Test
    void isValid_ShouldBeTrue_WhenFundsSufficient() {
        BigDecimal sufficientBalance = BigDecimal.valueOf(5);
        BigDecimal usageCost = BigDecimal.valueOf(2.5);

        RechargeableTicket ticket = ticketFactory.createRechargeableTicket("Rechargeable Ticket",
                UUID.randomUUID(),
                sufficientBalance,
                usageCost)
                .build();
        boolean valid = ticket.isValid();

        assertThat(valid).isTrue();
    }

    @Test
    void isValid_ShouldBeFalse_WhenFundsNotSufficient() {
        BigDecimal noBalance = BigDecimal.valueOf(0);
        BigDecimal usageCost = BigDecimal.valueOf(2.5);

        RechargeableTicket ticket = ticketFactory.createRechargeableTicket("Rechargeable Ticket",
                UUID.randomUUID(),
                noBalance,
                usageCost)
                .build();
        boolean valid = ticket.isValid();

        assertThat(valid).isFalse();
    }

}