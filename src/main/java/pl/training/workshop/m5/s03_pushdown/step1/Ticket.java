package pl.training.workshop.m5.s03_pushdown.step1;

import pl.training.workshop.shared.Money;

/**
 * Krok 1: bez zmian - baza nadal zna upgradeToVip().
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
