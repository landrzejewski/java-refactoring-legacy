package pl.training.workshop.m5.s03_pushdown.start;

import pl.training.workshop.shared.Money;

/** Start: jedyny bilet, który naprawdę korzysta z dopłaty VIP. */
public final class StandardTicket extends Ticket {
    public StandardTicket(Money basePrice) {
        super(basePrice);
    }

    @Override
    protected int discountPercent() {
        return 0;
    }
}
