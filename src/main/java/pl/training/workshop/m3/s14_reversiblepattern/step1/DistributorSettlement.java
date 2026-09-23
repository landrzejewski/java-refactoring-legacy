package pl.training.workshop.m3.s14_reversiblepattern.step1;

import java.math.BigDecimal;
import java.util.Map;

import pl.training.workshop.m3.s14_reversiblepattern.Deal;
import pl.training.workshop.m3.s14_reversiblepattern.FestivalTariffClient;

/**
 * Krok 1: Replace Conditional with Strategy. Rozliczenie tylko wybiera model;
 * każdy wariant zmienia się we własnej klasie. Uzasadnienie: dwa ISTNIEJĄCE warianty
 * z różnymi właścicielami i obcy interfejs festiwalu - nie przyszłe pluginy.
 */
public final class DistributorSettlement {
    private final Map<String, SettlementModel> models = Map.of(
            "PERCENT", new PercentageModel(),
            "FESTIVAL", new FestivalFeeAdapter(new FestivalTariffClient()));

    public BigDecimal payout(Deal deal, int week, BigDecimal ticketRevenue) {
        SettlementModel model = models.get(deal.model());
        if (model == null) {
            throw new IllegalArgumentException("nieznany model: " + deal.model());
        }
        return model.payout(deal, week, ticketRevenue);
    }
}
