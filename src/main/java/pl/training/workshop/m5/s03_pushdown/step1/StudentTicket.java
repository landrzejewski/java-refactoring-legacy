package pl.training.workshop.m5.s03_pushdown.step1;

import pl.training.workshop.shared.Money;

/** Krok 1: bez zmian - override rzucający wyjątek jeszcze jest. */
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
