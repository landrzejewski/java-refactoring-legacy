package pl.training.workshop.m5.s03_pushdown.step3;

import pl.training.workshop.shared.Money;

/**
 * Krok 3 (rozwiązanie): Push Members Down - upgradeToVip(), isVipUpgraded() i pole vipUpgraded
 * trafiły do StandardTicket. Baza obiecuje tylko to, co prawdziwe dla wszystkich biletów.
 */
public abstract class Ticket {
    private final Money basePrice;

    protected Ticket(Money basePrice) {
        this.basePrice = basePrice;
    }

    public Money price() {
        return basePrice.minus(basePrice.percent(discountPercent())).plus(surcharge());
    }

    protected Money surcharge() {
        return Money.ZERO;
    }

    protected abstract int discountPercent();
}
