package pl.training.workshop.m5.s03_pushdown.start;

import pl.training.workshop.shared.Money;

/** Start: odziedziczył operację, której nie może wykonać - łamie kontrakt bazy. */
public final class StudentTicket extends Ticket {
    public StudentTicket(Money basePrice) {
        super(basePrice);
    }

    @Override
    public void upgradeToVip() {
        throw new UnsupportedOperationException("bilet ulgowy nie ma dopłaty VIP");
    }

    @Override
    protected int discountPercent() {
        return 25;
    }
}
