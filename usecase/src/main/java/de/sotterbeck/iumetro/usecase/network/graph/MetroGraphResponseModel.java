package de.sotterbeck.iumetro.usecase.network.graph;

import java.util.List;

public record MetroGraphResponseModel(
        List<Node> nodes,
        List<Link> links
) {

    public record Node(String id, String name, String group) {

    }

    public record Link(String source, String target, List<String> lines) {

    }

}
