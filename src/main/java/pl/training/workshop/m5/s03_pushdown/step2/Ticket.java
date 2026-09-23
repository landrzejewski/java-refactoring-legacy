package pl.training.workshop.m5.s03_pushdown.step2;

import pl.training.workshop.shared.Money;

/**
 * Krok 2: najpierw przenosimy zachowanie korzystające z pola. Baza liczy cenę z punktem
 * rozszerzenia {@code surcharge()}; o dopłacie VIP decyduje już StandardTicket.
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
        return basePrice.minus(basePrice.percent(discountPercent())).plus(surcharge());
    }

    protected Money surcharge() {
        return Money.ZERO;
    }

    protected abstract int discountPercent();
}
