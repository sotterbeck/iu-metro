package de.sotterbeck.iumetro.entity.ticket.rechargeable;

import de.sotterbeck.iumetro.entity.reader.TicketReaderInfo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommonRechargeableTicketBuilder implements RechargeableTicketBuilder {

    private final String name;
    private final UUID id;
    private final BigDecimal balance;
    private final BigDecimal usageCost;
    private final List<TicketReaderInfo> usages = new ArrayList<>();
    private int paidUsages;

    public CommonRechargeableTicketBuilder(String name, UUID id, BigDecimal balance, BigDecimal usageCost) {
        this.name = name;
        this.id = id;
        this.balance = balance;
        this.usageCost = usageCost;
    }

    @Override
    public RechargeableTicketBuilder addUsage(TicketReaderInfo ticketReader) {
        usages.add(ticketReader);
        return this;
    }

    @Override
    public RechargeableTicketBuilder paidUsageLimit(int paidUsages) {
        this.paidUsages = paidUsages;
        return this;
    }

    @Override
    public RechargeableTicket build() {
        if (paidUsages != 0) {
            return new PaidUsageLimitedRechargeableTicket(name, id, balance, usageCost, paidUsages, usages);
        }
        return new SimpleRechargeableTicket(name, id, balance, usageCost, usages);
    }

}