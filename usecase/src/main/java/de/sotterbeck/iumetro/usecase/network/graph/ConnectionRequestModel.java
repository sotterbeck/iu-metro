package de.sotterbeck.iumetro.usecase.network.graph;

import de.sotterbeck.iumetro.usecase.network.line.LineConnectionDto;

import java.util.List;
import java.util.UUID;

public record ConnectionRequestModel(UUID station1Id, UUID station2Id, List<LineConnectionDto> lines) {

}
