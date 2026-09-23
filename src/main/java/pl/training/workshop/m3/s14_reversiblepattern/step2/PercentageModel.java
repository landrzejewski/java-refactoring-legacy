package pl.training.workshop.m3.s14_reversiblepattern.step2;

import java.math.BigDecimal;
import java.math.RoundingMode;

import pl.training.workshop.m3.s14_reversiblepattern.Deal;

/** Krok 1: procent od przychodu - tydzień 1: 50%, 2: 40%, dalej 35%; minimalna gwarancja 500.00. */
public final class PercentageModel implements SettlementModel {
    private static final BigDecimal MINIMUM_GUARANTEE = new BigDecimal("500.00");

    @Override
    public BigDecimal payout(Deal deal, int week, BigDecimal ticketRevenue) {
        int percent = week == 1 ? 50 : week == 2 ? 40 : 35;
        BigDecimal share = ticketRevenue.multiply(BigDecimal.valueOf(percent))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return share.max(MINIMUM_GUARANTEE);
    }
}
