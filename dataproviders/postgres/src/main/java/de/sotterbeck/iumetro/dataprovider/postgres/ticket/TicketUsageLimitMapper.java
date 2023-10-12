package de.sotterbeck.iumetro.dataprovider.postgres.ticket;

import jakarta.persistence.*;

import java.util.Objects;

@Entity(name = "TicketUsageLimit")
@Table(name = "ticket_usage_limits")
public class TicketUsageLimitMapper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "max_usages", unique = true)
    private Integer maxUsages;

    public TicketUsageLimitMapper() {
    }

    public TicketUsageLimitMapper(Integer maxUsages) {
        this.maxUsages = maxUsages;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMaxUsages() {
        return maxUsages;
    }

    public void setMaxUsages(Integer maxUsages) {
        this.maxUsages = maxUsages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TicketUsageLimitMapper that = (TicketUsageLimitMapper) o;
        return maxUsages == that.maxUsages;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maxUsages);
    }

}
