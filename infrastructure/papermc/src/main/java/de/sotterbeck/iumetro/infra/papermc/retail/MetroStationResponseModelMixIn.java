package de.sotterbeck.iumetro.infra.papermc.retail;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.sotterbeck.iumetro.app.common.PositionDto;

import java.util.Optional;

public abstract class MetroStationResponseModelMixIn {

    @JsonProperty
    abstract String id();

    @JsonProperty
    abstract String displayId();

    @JsonProperty
    abstract String name();

    @JsonProperty
    abstract Optional<PositionDto> position();

}
