package de.sotterbeck.iumetro.domain.ticket.rechargeable;

import de.sotterbeck.iumetro.domain.reader.TicketReaderInfo;
import de.sotterbeck.iumetro.domain.ticket.AbstractTicket;
import de.sotterbeck.iumetro.domain.ticket.constained.TicketUsageConstraint;
import de.sotterbeck.iumetro.domain.ticket.constained.UsageLimitConstraint;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

class PaidUsageLimitedRechargeableTicket extends AbstractTicket implements RechargeableTicket {

    private final RechargeableTicket delegate;
    private final TicketUsageConstraint paidUsageLimitConstraint;
    private final BigDecimal usageCost;

    public PaidUsageLimitedRechargeableTicket(String name,
                                              UUID id,
                                              BigDecimal balance,
                                              BigDecimal usageCost,
                                              int paidUsages,
                                              List<TicketReaderInfo> usages) {
        super(name, id, usages);
        this.usageCost = usageCost;
        this.delegate = new SimpleRechargeableTicket(name, id, balance, usageCost, usages);
        this.paidUsageLimitConstraint = new UsageLimitConstraint(paidUsages);
    }

    @Override
    public void onEntry(TicketReaderInfo ticketReader) {
        if (isPaidUsage()) {
            delegate.subtractBalance(usageCost);
        }
    }

    private boolean isPaidUsage() {
        return paidUsageLimitConstraint.isValid(this);
    }

    @Override
    public BigDecimal balance() {
        return delegate.balance();
    }

    @Override
    public void addBalance(BigDecimal amount) {
        delegate.addBalance(amount);
    }

    @Override
    public void subtractBalance(BigDecimal amount) {
        delegate.subtractBalance(amount);
    }

    @Override
    public boolean isValid() {
        return delegate.isValid();
    }

}
