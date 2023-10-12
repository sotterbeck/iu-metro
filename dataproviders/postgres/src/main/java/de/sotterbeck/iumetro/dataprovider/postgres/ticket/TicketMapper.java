package de.sotterbeck.iumetro.dataprovider.postgres.ticket;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.UUID;

@Entity(name = "Ticket")
@Table(name = "tickets")
public class TicketMapper {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST)
    @JoinColumn(name = "usage_limit_id", referencedColumnName = "id")
    private TicketUsageLimitMapper usageLimit;

    @ManyToOne(fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST)
    @JoinColumn(name = "time_limit_id", referencedColumnName = "id")
    private TicketTimeLimitMapper timeLimit;

    public TicketMapper() {
    }

    public TicketMapper(UUID id, String name, TicketUsageLimitMapper usageLimit, TicketTimeLimitMapper timeLimit) {
        this.id = id;
        this.name = name;
        this.usageLimit = usageLimit;
        this.timeLimit = timeLimit;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TicketUsageLimitMapper getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(TicketUsageLimitMapper usageLimit) {
        this.usageLimit = usageLimit;
    }

    public TicketTimeLimitMapper getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(TicketTimeLimitMapper timeLimit) {
        this.timeLimit = timeLimit;
    }

    @Override
    public String toString() {
        return "TicketMapper[" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", usageLimit=" + usageLimit +
                ", timeLimit=" + timeLimit +
                ']';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TicketMapper that = (TicketMapper) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
