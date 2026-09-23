package pl.training.workshop.m5.s03_pushdown.step1;

import pl.training.workshop.shared.Money;

/** Krok 1: bez zmian. */
public final class StandardTicket extends Ticket {
    public StandardTicket(Money basePrice) {
        super(basePrice);
    }

    @Override
    protected int discountPercent() {
        return 0;
    }
}
