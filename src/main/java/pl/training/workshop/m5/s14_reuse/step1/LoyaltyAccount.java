package pl.training.workshop.m5.s14_reuse.step1;

import pl.training.workshop.shared.Money;

/** Krok 1: konto deleguje arytmetykę punktów do PointsLedger; publiczne API bez zmian. */
public class LoyaltyAccount {
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

    public String owner() {
        return owner;
    }

    public int points() {
        return ledger.points();
    }
}
