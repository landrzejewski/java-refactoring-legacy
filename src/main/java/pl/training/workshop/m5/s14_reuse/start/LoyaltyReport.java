package pl.training.workshop.m5.s14_reuse.start;

/** Start: raport potrzebuje tylko właściciela i punktów - a przyjmuje całe LoyaltyAccount. */
public final class LoyaltyReport {
    public String line(LoyaltyAccount account) {
        return account.owner() + ": " + account.points() + " pkt";
    }
}
