package de.sotterbeck.iumetro.app.network.graph;

import java.util.Map;

public record StationNodeDto(String name, Map<String, Integer> neighbors) {

}
