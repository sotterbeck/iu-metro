package de.sotterbeck.iumetro.dataprovider.postgres.ticket;

import jakarta.persistence.*;

import java.time.Duration;
import java.util.Objects;

@Entity(name = "TicketTimeLimit")
@Table(name = "ticket_time_limits")
public class TicketTimeLimitMapper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "time_limit", unique = true)
    private Duration timeLimit;

    public TicketTimeLimitMapper() {
    }

    public TicketTimeLimitMapper(Duration timeLimit) {
        this.timeLimit = timeLimit;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Duration getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Duration timeLimit) {
        this.timeLimit = timeLimit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TicketTimeLimitMapper that = (TicketTimeLimitMapper) o;
        return Objects.equals(timeLimit, that.timeLimit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeLimit);
    }

}
