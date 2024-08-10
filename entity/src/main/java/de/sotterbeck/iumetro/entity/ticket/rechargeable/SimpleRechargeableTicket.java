package de.sotterbeck.iumetro.entity.ticket.rechargeable;

import de.sotterbeck.iumetro.entity.reader.TicketReaderInfo;
import de.sotterbeck.iumetro.entity.ticket.AbstractTicket;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

class SimpleRechargeableTicket extends AbstractTicket implements RechargeableTicket {

    private final BigDecimal usageCost;
    private BigDecimal balance;

    public SimpleRechargeableTicket(String name,
                                    UUID id,
                                    BigDecimal balance,
                                    BigDecimal usageCost,
                                    List<TicketReaderInfo> usages) {
        super(name, id, usages);
        this.balance = balance;
        this.usageCost = usageCost;
    }

    @Override
    public void onEntry(TicketReaderInfo ticketReader) {
        addUsage(ticketReader);
        subtractBalance(usageCost);
    }

    @Override
    public boolean isValid() {
        return isBalanceLargerThanZero();
    }

    private boolean isBalanceLargerThanZero() {
        return balance.signum() > 0;
    }

    @Override
    public BigDecimal balance() {
        return balance;
    }

    @Override
    public void subtractBalance(BigDecimal amount) {
        balance = balance.subtract(amount);
    }

    @Override
    public void addBalance(BigDecimal amount) {
        if (isNegativeAmount(amount)) {
            throw new IllegalArgumentException("Amount should be positive.");
        }
        balance = balance.add(amount);
    }

    private boolean isNegativeAmount(BigDecimal amount) {
        return amount.signum() <= 0;
    }

}
