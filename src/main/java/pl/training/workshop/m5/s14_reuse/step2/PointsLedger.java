package pl.training.workshop.m5.s14_reuse.step2;

import java.math.BigDecimal;

import pl.training.workshop.shared.Money;

/**
 * Krok 2: bez zmian - teraz używają jej dwie niezależne klasy.
 */
public final class PointsLedger {
    private int points;

    public void earn(Money paidForTickets) {
        points += paidForTickets.amount().divideToIntegralValue(BigDecimal.TEN).intValue();
    }

    public boolean spend(int amount) {
        if (points < amount) {
            return false;
        }
        points -= amount;
        return true;
    }

    public int points() {
        return points;
    }
}
