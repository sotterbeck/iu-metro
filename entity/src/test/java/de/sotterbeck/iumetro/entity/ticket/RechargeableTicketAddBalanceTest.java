package de.sotterbeck.iumetro.entity.ticket;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class RechargeableTicketAddBalanceTest {

    @Test
    void addBalance_ShouldAddBalance_WhenAmountIsPositive() {
        BigDecimal positiveAmount = BigDecimal.valueOf(5);
        RechargeableTicket ticket = new RechargeableTicketBuilder("Rechargable Ticket", UUID.randomUUID(), BigDecimal.valueOf(15), BigDecimal.TWO).build();

        ticket.addBalance(positiveAmount);
        BigDecimal balance = ticket.balance();

        assertThat(balance).isEqualTo("20");
    }

    @Test
    void addBalance_ShouldThrowException_WhenAmountIsNegative() {
        BigDecimal negativeAmount = BigDecimal.valueOf(-5);
        RechargeableTicket ticket = new RechargeableTicketBuilder("Rechargable Ticket", UUID.randomUUID(), BigDecimal.valueOf(15), BigDecimal.TWO).build();

        Throwable throwable = catchThrowable(() -> ticket.addBalance(negativeAmount));

        assertThat(throwable).isNotNull();
    }

}