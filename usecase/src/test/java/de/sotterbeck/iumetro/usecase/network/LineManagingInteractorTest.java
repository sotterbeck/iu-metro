package de.sotterbeck.iumetro.usecase.network;

import de.sotterbeck.iumetro.usecase.common.CommonPresenter;
import de.sotterbeck.iumetro.usecase.network.graph.MetroNetworkRepository;
import de.sotterbeck.iumetro.usecase.network.line.LineDto;
import de.sotterbeck.iumetro.usecase.network.line.LineManagingInteractor;
import de.sotterbeck.iumetro.usecase.network.line.LineRequestModel;
import de.sotterbeck.iumetro.usecase.network.line.LineResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LineManagingInteractorTest {

    @Mock
    private MetroNetworkRepository metroNetworkRepository;
    @Mock
    private CommonPresenter linePresenter;

    private LineManagingInteractor lineManagingInteractor;

    @BeforeEach
    void setUp() {
        lineManagingInteractor = new LineManagingInteractor(metroNetworkRepository, linePresenter);
    }

    @Test
    void getAll_ShouldReturnAllLinesFromRepository() {
        when(metroNetworkRepository.getAllLines()).thenReturn(List.of(
                new LineDto("M1", 0x0284c7, List.of()),
                new LineDto("M2", 0xdc2626, List.of())
        ));

        List<LineResponseModel> response = lineManagingInteractor.getAllLines();

        assertThat(response).containsExactly(
                new LineResponseModel("M1", "#0284c7", List.of()),
                new LineResponseModel("M2", "#dc2626", List.of())
        );
    }

    @Test
    void createLine_ShouldPresentFailView_WhenLineNameAlreadyExists() {
        String lineName = "M1";
        when(metroNetworkRepository.existsLineByName(lineName)).thenReturn(true);
        LineRequestModel request = new LineRequestModel(lineName, "#0284c7");

        lineManagingInteractor.createLine(request);

        verify(linePresenter).prepareFailView("Line with name " + lineName + " already exists");

    }

    @Test
    void createLine_ShouldPresentFailView_WhenColorIsInvalid() {
        String lineName = "M1";
        when(metroNetworkRepository.existsLineByName(lineName)).thenReturn(false);
        LineRequestModel request = new LineRequestModel(lineName, "");

        lineManagingInteractor.createLine(request);

        verify(linePresenter).prepareFailView("Line color " + request.color() + " is not a valid hex color");
    }

    @Test
    void createLine_ShouldSaveLine_WhenLineDoesNotExist() {
        String lineName = "M1";
        when(metroNetworkRepository.existsLineByName(lineName)).thenReturn(false);
        LineRequestModel request = new LineRequestModel(lineName, "#0284c7");

        lineManagingInteractor.createLine(request);

        verify(metroNetworkRepository).saveLine(request.name(), 0x0284c7);
    }

    @Test
    void removeLine_ShouldPresentFailView_WhenLineDoesNotExist() {
        String lineName = "M1";
        when(metroNetworkRepository.existsLineByName(lineName)).thenReturn(false);

        lineManagingInteractor.removeLine(lineName);

        verify(linePresenter).prepareFailView("Line with name " + lineName + " does not exist");
    }

    @Test
    void deleteLine_ShouldRemoveLine_WhenLineExists() {
        String lineName = "M1";
        when(metroNetworkRepository.existsLineByName(lineName)).thenReturn(true);

        lineManagingInteractor.removeLine(lineName);

        verify(metroNetworkRepository).removeLineByName(lineName);

    }

}