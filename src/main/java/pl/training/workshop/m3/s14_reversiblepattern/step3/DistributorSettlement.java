package pl.training.workshop.m3.s14_reversiblepattern.step3;

import java.math.BigDecimal;
import java.math.RoundingMode;

import pl.training.workshop.m3.s14_reversiblepattern.Deal;

/**
 * Krok 3 (rozwiązanie): refaktoryzacja OD wzorca. Inline Class PercentageModel,
 * Safe Delete interfejsu SettlementModel i mapy. Jeden algorytm = jedna metoda.
 * Gdy wróci drugi model, Strategy da się przywrócić tymi samymi krokami w przód.
 */
public final class DistributorSettlement {
    private static final BigDecimal MINIMUM_GUARANTEE = new BigDecimal("500.00");

    public BigDecimal payout(Deal deal, int week, BigDecimal ticketRevenue) {
        if (!deal.model().equals("PERCENT")) {
            throw new IllegalArgumentException("nieznany model: " + deal.model());
        }
        int percent = week == 1 ? 50 : week == 2 ? 40 : 35;
        BigDecimal share = ticketRevenue.multiply(BigDecimal.valueOf(percent))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return share.max(MINIMUM_GUARANTEE);
    }
}
