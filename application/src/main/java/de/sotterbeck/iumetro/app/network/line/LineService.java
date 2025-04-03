package de.sotterbeck.iumetro.app.network.line;

import de.sotterbeck.iumetro.app.common.CommonPresenter;
import de.sotterbeck.iumetro.app.network.graph.MetroNetworkRepository;
import de.sotterbeck.iumetro.domain.common.Color;

import java.util.List;
import java.util.UUID;

public class LineService {

    private final MetroNetworkRepository metroNetworkRepository;
    private final CommonPresenter linePresenter;

    public LineService(MetroNetworkRepository metroNetworkRepository, CommonPresenter linePresenter) {
        this.metroNetworkRepository = metroNetworkRepository;
        this.linePresenter = linePresenter;
    }

    public List<LineResponseModel> getAllLines() {
        List<LineDto> lineDtos = metroNetworkRepository.getAllLines();
        return lineDtos.stream()
                .map(LineService::toResponseModel)
                .toList();
    }

    public void createLine(LineRequestModel lineRequestModel) {
        if (metroNetworkRepository.existsLineByName(lineRequestModel.name())) {
            linePresenter.prepareFailView("Line with name " + lineRequestModel.name() + " already exists");
            return;
        }

        if (!Color.isValidHexColor(lineRequestModel.color())) {
            linePresenter.prepareFailView("Line color " + lineRequestModel.color() + " is not a valid hex color");
            return;
        }

        Color color = Color.ofHex(lineRequestModel.color());

        metroNetworkRepository.saveLine(lineRequestModel.name(), color.value());
    }

    public void removeLine(String name) {
        if (!metroNetworkRepository.existsLineByName(name)) {
            linePresenter.prepareFailView("Line with name " + name + " does not exist");
            return;
        }

        metroNetworkRepository.removeLineByName(name);
    }

    private static LineResponseModel toResponseModel(LineDto dto) {
        List<String> ids = dto.metroStationIds().stream()
                .map(UUID::toString)
                .toList();

        Color color = Color.ofValue(dto.color());

        return new LineResponseModel(dto.name(), color.hex(), ids);
    }

}
