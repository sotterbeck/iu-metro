package de.sotterbeck.iumetro.app.network.line;

import java.util.List;

public record LineResponseModel(String name, String color, List<String> stationIds) {

}
