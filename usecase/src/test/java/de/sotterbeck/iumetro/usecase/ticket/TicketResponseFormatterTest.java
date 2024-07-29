package de.sotterbeck.iumetro.usecase.ticket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TicketResponseFormatterTest {

    private TicketPresenter underTest;

    @BeforeEach
    void setUp() {
        underTest = new TicketResponseFormatter();
    }

    @Test
    void shouldFormatShortId() {
        String id = "d83f09f4-8275-441e-96e5-b290f55a8bf8";
        var ticket = new TicketRequestModel(UUID.fromString(id), "Ticket", 0, Duration.ZERO);

        TicketResponseModel formattedResponse = underTest.prepareSuccessView(ticket);

        assertThat(formattedResponse.shortId()).isEqualTo("d83f09f4");
    }

}