package pl.training.workshop.m5.s14_reuse.step1;

/** Krok 1: bez zmian. */
public final class LoyaltyReport {
    public String line(LoyaltyAccount account) {
        return account.owner() + ": " + account.points() + " pkt";
    }
}
