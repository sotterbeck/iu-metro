package de.sotterbeck.iumetro.entity.ticket;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RechargeableTicketBuilder {

    private final String name;
    private final UUID id;
    private final BigDecimal balance;
    private final BigDecimal usageCost;
    private final List<TicketReaderInfo> usages = new ArrayList<>();
    private int paidUsages;

    public RechargeableTicketBuilder(String name, UUID id, BigDecimal balance, BigDecimal usageCost) {
        this.name = name;
        this.id = id;
        this.balance = balance;
        this.usageCost = usageCost;
    }

    public RechargeableTicketBuilder addUsage(TicketReaderInfo ticketReader) {
        usages.add(ticketReader);
        return this;
    }

    public RechargeableTicketBuilder paidUsageLimit(int paidUsages) {
        this.paidUsages = paidUsages;
        return this;
    }

    public RechargeableTicket build() {
        if (paidUsages != 0) {
            return new PaidUsageLimitedRechargeableTicket(name, id, balance, usageCost, paidUsages, usages);
        }
        return new SimpleRechargeableTicket(name, id, balance, usageCost, usages);
    }

}