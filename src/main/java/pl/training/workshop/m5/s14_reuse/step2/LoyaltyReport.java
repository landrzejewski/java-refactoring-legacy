package pl.training.workshop.m5.s14_reuse.step2;

/** Krok 2: raport zależy od roli PointsHolder - przyjmuje oba konta bez fałszywego podtypowania. */
public final class LoyaltyReport {
    public String line(PointsHolder account) {
        return account.owner() + ": " + account.points() + " pkt";
    }
}
