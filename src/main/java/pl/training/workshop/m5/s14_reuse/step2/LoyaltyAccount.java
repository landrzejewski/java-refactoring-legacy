package pl.training.workshop.m5.s14_reuse.step2;

import pl.training.workshop.shared.Money;

/** Krok 2: konto klienta implementuje rolę PointsHolder i nadal wymienia punkty na bilety. */
public final class LoyaltyAccount implements PointsHolder {
    private final String owner;
    private final PointsLedger ledger = new PointsLedger();

    public LoyaltyAccount(String owner) {
        this.owner = owner;
    }

    public void earn(Money paidForTickets) {
        ledger.earn(paidForTickets);
    }

    public boolean redeemFreeTicket() {
        return ledger.spend(100);
    }

    @Override
    public String owner() {
        return owner;
    }

    @Override
    public int points() {
        return ledger.points();
    }
}
