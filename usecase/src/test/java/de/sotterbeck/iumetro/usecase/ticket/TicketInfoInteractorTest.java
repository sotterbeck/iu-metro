package de.sotterbeck.iumetro.usecase.ticket;

import de.sotterbeck.iumetro.usecase.faregate.UsageDto;
import de.sotterbeck.iumetro.usecase.faregate.UsageResponseModel;
import de.sotterbeck.iumetro.usecase.faregate.UsageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketInfoInteractorTest {

    @Mock
    private TicketRepository ticketRepository;

    private TicketInfoInteractor underTest;

    @BeforeEach
    void setUp() {
        underTest = new TicketInfoInteractorImpl(ticketRepository);
    }

    @Test
    void exists_ShouldReturnFalse_WhenTicketDoesNotExist() {
        String id = "02c1a3c5-979c-4845-9315-e96ffe8aa6eb";
        when(ticketRepository.existsById(UUID.fromString(id))).thenReturn(false);

        boolean exists = underTest.exists(id);

        assertThat(exists).isFalse();
    }

    @Test
    void exists_ShouldReturnTrue_WhenTicketDoesExist() {
        String id = "02c1a3c5-979c-4845-9315-e96ffe8aa6eb";
        when(ticketRepository.existsById(UUID.fromString(id))).thenReturn(true);

        boolean exists = underTest.exists(id);

        assertThat(exists).isTrue();
    }

    @Test
    void usages_ShouldReturnNoUsages_WhenTicketHasNoUsages() {
        UUID id = UUID.fromString("02c1a3c5-979c-4845-9315-e96ffe8aa6eb");

        List<UsageResponseModel> usages = underTest.usages(id);

        assertThat(usages).isEmpty();
    }

    @Test
    void usages_ShouldReturnMultipleUsages_WhenTicketHasUsages() {
        UUID id = UUID.fromString("02c1a3c5-979c-4845-9315-e96ffe8aa6eb");
        List<UsageDto> dsUsages = List.of(
                new UsageDto("firstStation", ZonedDateTime.now(), UsageType.ENTRY),
                new UsageDto("secondStation", ZonedDateTime.now().plusHours(1), UsageType.EXIT)
        );

        when(ticketRepository.getTicketUsages(id)).thenReturn(dsUsages);
        List<UsageResponseModel> usages = underTest.usages(id);

        assertThat(usages).hasSize(2);

    }

}