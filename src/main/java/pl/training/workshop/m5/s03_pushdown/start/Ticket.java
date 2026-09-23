package pl.training.workshop.m5.s03_pushdown.start;

import pl.training.workshop.shared.Money;

/**
 * Start: baza obiecuje dopłatę VIP wszystkim biletom, choć sensowna jest tylko dla biletu normalnego.
 * Sygnały: override rzucający UnsupportedOperationException i klient sprawdzający instanceof.
 */
public abstract class Ticket {
    private final Money basePrice;
    private boolean vipUpgraded;

    protected Ticket(Money basePrice) {
        this.basePrice = basePrice;
    }

    public void upgradeToVip() {
        vipUpgraded = true;
    }

    public boolean isVipUpgraded() {
        return vipUpgraded;
    }

    public Money price() {
        Money price = basePrice.minus(basePrice.percent(discountPercent()));
        return isVipUpgraded() ? price.plus(Money.of("10.00")) : price;
    }

    protected abstract int discountPercent();
}
