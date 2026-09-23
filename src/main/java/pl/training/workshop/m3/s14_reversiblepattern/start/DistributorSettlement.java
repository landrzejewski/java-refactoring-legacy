package pl.training.workshop.m3.s14_reversiblepattern.start;

import java.math.BigDecimal;
import java.math.RoundingMode;

import pl.training.workshop.m3.s14_reversiblepattern.Deal;
import pl.training.workshop.m3.s14_reversiblepattern.FestivalTariffClient;

/**
 * Start: dwa istniejące modele rozliczeń w jednym switchu. Model festiwalowy
 * dodatkowo tłumaczy obcy interfejs (grosze w long) w środku logiki rozliczeń.
 * Oba warianty istnieją dziś i zmieniają się niezależnie - to uzasadnia wzorzec.
 */
public final class DistributorSettlement {
    private final FestivalTariffClient festival = new FestivalTariffClient();

    public BigDecimal payout(Deal deal, int week, BigDecimal ticketRevenue) {
        switch (deal.model()) {
            case "PERCENT" -> {
                int percent = week == 1 ? 50 : week == 2 ? 40 : 35;
                BigDecimal share = ticketRevenue.multiply(BigDecimal.valueOf(percent))
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                return share.max(new BigDecimal("500.00"));
            }
            case "FESTIVAL" -> {
                long cents = festival.weeklyFeeInCents(deal.title(), week);
                return BigDecimal.valueOf(cents, 2);
            }
            default -> throw new IllegalArgumentException("nieznany model: " + deal.model());
        }
    }
}
