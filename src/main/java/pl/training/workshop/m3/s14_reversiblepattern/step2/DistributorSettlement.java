package pl.training.workshop.m3.s14_reversiblepattern.step2;

import java.math.BigDecimal;
import java.util.Map;

import pl.training.workshop.m3.s14_reversiblepattern.Deal;

/**
 * Krok 2: wariant znika - umowy festiwalowe wygasły. Safe Delete FestivalFeeAdapter
 * i wpisu w mapie. Zostaje Strategy z JEDNĄ implementacją: sygnał nadmiaru wzorca.
 * (To zmiana zachowania: model FESTIVAL jest teraz odrzucany.)
 */
public final class DistributorSettlement {
    private final Map<String, SettlementModel> models = Map.of(
            "PERCENT", new PercentageModel());

    public BigDecimal payout(Deal deal, int week, BigDecimal ticketRevenue) {
        SettlementModel model = models.get(deal.model());
        if (model == null) {
            throw new IllegalArgumentException("nieznany model: " + deal.model());
        }
        return model.payout(deal, week, ticketRevenue);
    }
}
