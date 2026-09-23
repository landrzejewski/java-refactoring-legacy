package pl.training.workshop.m5.s03_pushdown.step2;

import pl.training.workshop.shared.Money;

/** Krok 2: dopłata VIP liczona w podklasie (override surcharge()) - baza nie czyta już pola. */
public final class StandardTicket extends Ticket {
    public StandardTicket(Money basePrice) {
        super(basePrice);
    }

    @Override
    protected int discountPercent() {
        return 0;
    }

    @Override
    protected Money surcharge() {
        return isVipUpgraded() ? Money.of("10.00") : Money.ZERO;
    }
}
