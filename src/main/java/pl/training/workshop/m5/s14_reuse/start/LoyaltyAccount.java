package pl.training.workshop.m5.s14_reuse.start;

import java.math.BigDecimal;

import pl.training.workshop.shared.Money;

/** Start: konto lojalnościowe klienta - 1 pkt za pełne 10.00, 100 pkt = darmowy bilet 2D. */
public class LoyaltyAccount {
    private final String owner;
    private int points;

    public LoyaltyAccount(String owner) {
        this.owner = owner;
    }

    public void earn(Money paidForTickets) {
        points += paidForTickets.amount().divideToIntegralValue(BigDecimal.TEN).intValue();
    }

    public boolean redeemFreeTicket() {
        if (points < 100) {
            return false;
        }
        points -= 100;
        return true;
    }

    public String owner() {
        return owner;
    }

    public int points() {
        return points;
    }
}
