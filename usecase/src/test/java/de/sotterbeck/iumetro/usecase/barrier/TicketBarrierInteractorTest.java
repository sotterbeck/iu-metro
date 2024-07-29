package de.sotterbeck.iumetro.usecase.barrier;

import de.sotterbeck.iumetro.usecase.ticket.TicketDsGateway;
import de.sotterbeck.iumetro.usecase.ticket.TicketDsModel;
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

import static de.sotterbeck.iumetro.usecase.barrier.TicketBarrierInteractor.BarrierType.ENTRY_BARRIER;
import static de.sotterbeck.iumetro.usecase.barrier.TicketBarrierInteractor.BarrierType.EXIT_BARRIER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketBarrierInteractorTest {

    @Mock
    private TicketDsGateway ticketDsGateway;

    private UUID id;
    private UsageRequestModel entry;
    private TicketBarrierInteractor underTest;

    @BeforeEach
    void setUp() {
        underTest = new TicketBarrierInteractor(ticketDsGateway, ENTRY_BARRIER);
        id = UUID.fromString("2551ea6c-e951-41a8-b89e-deea9a5b7241");
        entry = new UsageRequestModel("station", ZonedDateTime.now(), UsageType.ENTRY);
    }

    @Test
    void addUsage_ShouldAddUsageToTicket_WhenTicketExists() {
        when(ticketDsGateway.existsById(id)).thenReturn(true);
        underTest.addUsageToTicket(id, entry);

        then(ticketDsGateway).should(times(1)).saveTicketUsage(any(), any());
    }

    @Test
    void addUsage_ShouldThrowException_WhenTicketIdDoesNotExist() {
        when(ticketDsGateway.existsById(id)).thenReturn(false);
        Throwable thrown = catchThrowable(() -> underTest.addUsageToTicket(id, entry));

        assertThat(thrown).isNotNull();
        then(ticketDsGateway).should(times(0)).saveTicketUsage(any(), any());
    }

    @Test
    void canOpenGate_ShouldReturnFalse_WhenTicketIdDoesNotExist() {
        when(ticketDsGateway.existsById(id)).thenReturn(false);
        boolean canOpen = underTest.canOpen(id, entry);

        assertThat(canOpen).isFalse();
    }

    @Test
    void canFineUser_ShouldReturnFalse_WhenTicketWithIdDoesNotExist() {
        when(ticketDsGateway.existsById(id)).thenReturn(false);
        boolean canFineUser = underTest.canFineUser(id, entry);

        assertThat(canFineUser).isFalse();
    }

    @Nested
    class GivenEntryBarrier {

        @BeforeEach
        void setUp() {
            underTest = new TicketBarrierInteractor(ticketDsGateway, ENTRY_BARRIER);
        }

        @Test
        void canOpenGate_ShouldReturnTrue_WhenTicketIsValid() {
            when(ticketDsGateway.existsById(id)).thenReturn(true);
            when(ticketDsGateway.get(id)).thenReturn(Optional.of(new TicketDsModel(id, "ticket")));
            boolean canOpen = underTest.canOpen(id, entry);

            assertThat(canOpen).isTrue();
        }

        @Test
        void canOpenGate_ShouldReturnFalse_WhenTicketIsInvalid() {
            List<UsageDsModel> oneUsage = List.of(
                    new UsageDsModel("station", ZonedDateTime.now().minusHours(1), UsageType.ENTRY),
                    new UsageDsModel("station", ZonedDateTime.now(), UsageType.EXIT)
            );

            when(ticketDsGateway.existsById(id)).thenReturn(true);
            when(ticketDsGateway.get(id)).thenReturn(Optional.of(new TicketDsModel(id, "ticket", 1, Duration.ZERO)));
            when(ticketDsGateway.getTicketUsages(id)).thenReturn(oneUsage);
            boolean canOpen = underTest.canOpen(id, entry);

            assertThat(canOpen).isFalse();
        }

        @Test
        void canFineUser_ShouldReturnFalse_WhenTicketExists() {
            when(ticketDsGateway.existsById(id)).thenReturn(true);
            when(ticketDsGateway.get(id)).thenReturn(Optional.of(new TicketDsModel(id, "ticket")));
            boolean canFineUser = underTest.canFineUser(id, entry);

            assertThat(canFineUser).isFalse();
        }
    }

    @Nested
    class GivenExitBarrier {

        @BeforeEach
        void setUp() {
            underTest = new TicketBarrierInteractor(ticketDsGateway, EXIT_BARRIER);
        }

        @Test
        void canOpenGate_ShouldReturnTrue_WhenTicketIsValid() {
            when(ticketDsGateway.existsById(id)).thenReturn(true);
            when(ticketDsGateway.get(id)).thenReturn(Optional.of(new TicketDsModel(id, "ticket")));
            boolean canOpen = underTest.canOpen(id, entry);

            assertThat(canOpen).isTrue();
        }

        @Test
        void canOpenGate_ShouldReturnTrue_WhenTicketIsInvalid() {
            List<UsageDsModel> oneUsage = List.of(
                    new UsageDsModel("station", ZonedDateTime.now().minusHours(1), UsageType.ENTRY),
                    new UsageDsModel("station", ZonedDateTime.now(), UsageType.EXIT)
            );

            when(ticketDsGateway.existsById(id)).thenReturn(true);
            when(ticketDsGateway.get(id)).thenReturn(Optional.of(new TicketDsModel(id, "ticket", 1, Duration.ZERO)));
            when(ticketDsGateway.getTicketUsages(id)).thenReturn(oneUsage);
            boolean canOpen = underTest.canOpen(id, entry);

            assertThat(canOpen).isTrue();
        }

        @Test
        void canFineUser_ShouldReturnFalse_WhenTheTicketsPreviousUsageWasEntry() {
            UsageDsModel entry = new UsageDsModel("station", ZonedDateTime.now().minusHours(1), UsageType.ENTRY);

            when(ticketDsGateway.existsById(id)).thenReturn(true);
            when(ticketDsGateway.get(id)).thenReturn(Optional.of(new TicketDsModel(id, "ticket", 1, Duration.ZERO)));
            when(ticketDsGateway.getTicketUsages(id)).thenReturn(List.of(entry));
            boolean canFineUser = underTest.canFineUser(id, new UsageRequestModel("station", ZonedDateTime.now(), UsageType.EXIT));

            assertThat(canFineUser).isFalse();
        }

        @Test
        void canFineUser_ShouldReturnTrue_WhenTheTicketsPreviousUsageWasExit() {
            UsageDsModel exit = new UsageDsModel("station", ZonedDateTime.now().minusHours(1), UsageType.EXIT);

            when(ticketDsGateway.existsById(id)).thenReturn(true);
            when(ticketDsGateway.get(id)).thenReturn(Optional.of(new TicketDsModel(id, "ticket", 1, Duration.ZERO)));
            when(ticketDsGateway.getTicketUsages(id)).thenReturn(List.of(exit));
            boolean canFineUser = underTest.canFineUser(id, new UsageRequestModel("station", ZonedDateTime.now(), UsageType.EXIT));

            assertThat(canFineUser).isTrue();
        }

    }

}