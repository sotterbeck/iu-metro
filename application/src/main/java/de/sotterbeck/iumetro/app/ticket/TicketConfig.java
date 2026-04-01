package de.sotterbeck.iumetro.app.ticket;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

public record TicketConfig(List<Constraint> constraints) {

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = UsageLimit.class, name = "usage_limit"),
            @JsonSubTypes.Type(value = TimeLimit.class, name = "time_limit")
    })
    public sealed interface Constraint permits UsageLimit, TimeLimit {

    }

    public record UsageLimit(int maxUsages) implements Constraint {

    }

    public record TimeLimit(String duration) implements Constraint {

    }

}
