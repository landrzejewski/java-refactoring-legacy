package pl.training.workshop.m5.s14_reuse.step2;

import pl.training.workshop.shared.Money;

/**
 * Krok 2 (rozwiązanie): Replace Inheritance with Delegation - konto firmowe ma własny PointsLedger
 * i NIE jest LoyaltyAccount. Nie ma czego blokować wyjątkiem: redeemFreeTicket() po prostu nie istnieje.
 */
public final class CorporateAccount implements PointsHolder {
    private final String company;
    private final PointsLedger ledger = new PointsLedger();

    public CorporateAccount(String company) {
        this.company = company;
    }

    public void earn(Money paidForTickets) {
        ledger.earn(paidForTickets);
    }

    @Override
    public String owner() {
        return company;
    }

    @Override
    public int points() {
        return ledger.points();
    }
}
