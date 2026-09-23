package pl.training.workshop.m5.s03_pushdown.step3;

import pl.training.workshop.shared.Money;

/** Krok 3: stan i operacja VIP żyją tam, gdzie mają sens. */
public final class StandardTicket extends Ticket {
    private boolean vipUpgraded;

    public StandardTicket(Money basePrice) {
        super(basePrice);
    }

    public void upgradeToVip() {
        vipUpgraded = true;
    }

    public boolean isVipUpgraded() {
        return vipUpgraded;
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
