package de.sotterbeck.iumetro.entity.ticket;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TicketConstraintTest {

    @Test
    void isValid_ShouldBeTrue_WhenEmployeeTicket() {
        TicketFactory ticketFactory = new SimpleTicketFactory();
        Ticket ticket = ticketFactory.createConstrainedTicket("Employee Ticket", UUID.randomUUID())
                .build();

        boolean valid = ticket.isValid();

        assertThat(valid).isTrue();
    }

}