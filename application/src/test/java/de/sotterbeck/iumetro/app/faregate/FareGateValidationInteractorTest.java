package de.sotterbeck.iumetro.app.faregate;

import de.sotterbeck.iumetro.app.ticket.TicketDto;
import de.sotterbeck.iumetro.app.ticket.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static de.sotterbeck.iumetro.app.faregate.FareGateValidationInteractor.BarrierType.ENTRY;
import static de.sotterbeck.iumetro.app.faregate.FareGateValidationInteractor.BarrierType.EXIT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FareGateValidationInteractorTest {

    @Mock
    private TicketRepository ticketRepository;

    private UUID id;
    private UsageRequestModel entry;
    private FareGateValidationInteractor underTest;

    @BeforeEach
    void setUp() {
        underTest = new FareGateValidationInteractor(ticketRepository, ENTRY);
        id = UUID.fromString("2551ea6c-e951-41a8-b89e-deea9a5b7241");
        entry = new UsageRequestModel("station", ZonedDateTime.now(), UsageType.ENTRY);
    }

    @Test
    void addUsage_ShouldAddUsageToTicket_WhenTicketExists() {
        when(ticketRepository.existsById(id)).thenReturn(true);
        underTest.addUsageToTicket(id, entry);

        then(ticketRepository).should(times(1)).saveTicketUsage(any(), any());
    }

    @Test
    void addUsage_ShouldThrowException_WhenTicketIdDoesNotExist() {
        when(ticketRepository.existsById(id)).thenReturn(false);
        Throwable thrown = catchThrowable(() -> underTest.addUsageToTicket(id, entry));

        assertThat(thrown).isNotNull();
        then(ticketRepository).should(times(0)).saveTicketUsage(any(), any());
    }

    @Test
    void canOpenGate_ShouldReturnFalse_WhenTicketIdDoesNotExist() {
        when(ticketRepository.existsById(id)).thenReturn(false);
        boolean canOpen = underTest.canOpen(id, entry);

        assertThat(canOpen).isFalse();
    }

    @Test
    void canFineUser_ShouldReturnFalse_WhenTicketWithIdDoesNotExist() {
        when(ticketRepository.existsById(id)).thenReturn(false);
        boolean canFineUser = underTest.canFineUser(id, entry);

        assertThat(canFineUser).isFalse();
    }

    @Nested
    class GivenEntryBarrier {

        @BeforeEach
        void setUp() {
            underTest = new FareGateValidationInteractor(ticketRepository, ENTRY);
        }

        @Test
        void canOpenGate_ShouldReturnTrue_WhenTicketIsValid() {
            when(ticketRepository.existsById(id)).thenReturn(true);
            when(ticketRepository.get(id)).thenReturn(Optional.of(new TicketDto(id, "ticket")));
            boolean canOpen = underTest.canOpen(id, entry);

            assertThat(canOpen).isTrue();
        }

        @Test
        void canOpenGate_ShouldReturnFalse_WhenTicketIsInvalid() {
            List<UsageDto> oneUsage = List.of(
                    new UsageDto("station", ZonedDateTime.now().minusHours(1), UsageType.ENTRY),
                    new UsageDto("station", ZonedDateTime.now(), UsageType.EXIT)
            );

            when(ticketRepository.existsById(id)).thenReturn(true);
            when(ticketRepository.get(id)).thenReturn(Optional.of(new TicketDto(id, "ticket", 1, Duration.ZERO)));
            when(ticketRepository.getTicketUsages(id)).thenReturn(oneUsage);
            boolean canOpen = underTest.canOpen(id, entry);

            assertThat(canOpen).isFalse();
        }

        @Test
        void canFineUser_ShouldReturnFalse_WhenTicketExists() {
            when(ticketRepository.existsById(id)).thenReturn(true);
            when(ticketRepository.get(id)).thenReturn(Optional.of(new TicketDto(id, "ticket")));
            boolean canFineUser = underTest.canFineUser(id, entry);

            assertThat(canFineUser).isFalse();
        }
    }

    @Nested
    class GivenExitBarrier {

        @BeforeEach
        void setUp() {
            underTest = new FareGateValidationInteractor(ticketRepository, EXIT);
        }

        @Test
        void canOpenGate_ShouldReturnTrue_WhenTicketIsValid() {
            when(ticketRepository.existsById(id)).thenReturn(true);
            when(ticketRepository.get(id)).thenReturn(Optional.of(new TicketDto(id, "ticket")));
            boolean canOpen = underTest.canOpen(id, entry);

            assertThat(canOpen).isTrue();
        }

        @Test
        void canOpenGate_ShouldReturnTrue_WhenTicketIsInvalid() {
            List<UsageDto> oneUsage = List.of(
                    new UsageDto("station", ZonedDateTime.now().minusHours(1), UsageType.ENTRY),
                    new UsageDto("station", ZonedDateTime.now(), UsageType.EXIT)
            );

            when(ticketRepository.existsById(id)).thenReturn(true);
            when(ticketRepository.get(id)).thenReturn(Optional.of(new TicketDto(id, "ticket", 1, Duration.ZERO)));
            when(ticketRepository.getTicketUsages(id)).thenReturn(oneUsage);
            boolean canOpen = underTest.canOpen(id, entry);

            assertThat(canOpen).isTrue();
        }

        @Test
        void canFineUser_ShouldReturnFalse_WhenTheTicketsPreviousUsageWasEntry() {
            UsageDto entry = new UsageDto("station", ZonedDateTime.now().minusHours(1), UsageType.ENTRY);

            when(ticketRepository.existsById(id)).thenReturn(true);
            when(ticketRepository.get(id)).thenReturn(Optional.of(new TicketDto(id, "ticket", 1, Duration.ZERO)));
            when(ticketRepository.getTicketUsages(id)).thenReturn(List.of(entry));
            boolean canFineUser = underTest.canFineUser(id, new UsageRequestModel("station", ZonedDateTime.now(), UsageType.EXIT));

            assertThat(canFineUser).isFalse();
        }

        @Test
        void canFineUser_ShouldReturnTrue_WhenTheTicketsPreviousUsageWasExit() {
            UsageDto exit = new UsageDto("station", ZonedDateTime.now().minusHours(1), UsageType.EXIT);

            when(ticketRepository.existsById(id)).thenReturn(true);
            when(ticketRepository.get(id)).thenReturn(Optional.of(new TicketDto(id, "ticket", 1, Duration.ZERO)));
            when(ticketRepository.getTicketUsages(id)).thenReturn(List.of(exit));
            boolean canFineUser = underTest.canFineUser(id, new UsageRequestModel("station", ZonedDateTime.now(), UsageType.EXIT));

            assertThat(canFineUser).isTrue();
        }

    }

}