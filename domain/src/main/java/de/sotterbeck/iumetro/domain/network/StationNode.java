package de.sotterbeck.iumetro.domain.network;

import java.util.Map;

public record StationNode(String id, String name, Map<String, Integer> neighbors) {

    public boolean isConnected() {
        return !neighbors.isEmpty();
    }

}
