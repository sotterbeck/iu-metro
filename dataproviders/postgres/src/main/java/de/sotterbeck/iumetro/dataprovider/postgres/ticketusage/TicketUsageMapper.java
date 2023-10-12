package de.sotterbeck.iumetro.dataprovider.postgres.ticketusage;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "TicketUsage")
@Table(name = "ticket_usages")
public class TicketUsageMapper {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "usage_type")
    @Enumerated(EnumType.STRING)
    private DsUsageType usageType;

    public TicketUsageMapper() {
    }

    public TicketUsageMapper(LocalDateTime timestamp, DsUsageType usageType) {
        this.timestamp = timestamp;
        this.usageType = usageType;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public DsUsageType getUsageType() {
        return usageType;
    }

    public void setUsageType(DsUsageType usageType) {
        this.usageType = usageType;
    }

    public enum DsUsageType {
        ENTER,
        EXIT

    }

}
