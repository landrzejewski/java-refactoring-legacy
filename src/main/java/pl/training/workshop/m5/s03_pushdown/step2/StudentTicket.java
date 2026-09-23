package pl.training.workshop.m5.s03_pushdown.step2;

import pl.training.workshop.shared.Money;

/** Krok 2: bez zmian. */
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
