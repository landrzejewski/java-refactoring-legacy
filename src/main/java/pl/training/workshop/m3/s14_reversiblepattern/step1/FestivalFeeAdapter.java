package pl.training.workshop.m3.s14_reversiblepattern.step1;

import java.math.BigDecimal;

import pl.training.workshop.m3.s14_reversiblepattern.Deal;
import pl.training.workshop.m3.s14_reversiblepattern.FestivalTariffClient;

/**
 * Krok 1: Adapter - tłumaczy obcy interfejs (grosze w long) na kontrakt
 * {@link SettlementModel}. Ma realną pracę: konwersję jednostek. Przychód z biletów
 * ignoruje - w tym modelu opłata nie zależy od sprzedaży.
 */
public final class FestivalFeeAdapter implements SettlementModel {
    private final FestivalTariffClient client;

    public FestivalFeeAdapter(FestivalTariffClient client) {
        this.client = client;
    }

    @Override
    public BigDecimal payout(Deal deal, int week, BigDecimal ticketRevenue) {
        return BigDecimal.valueOf(client.weeklyFeeInCents(deal.title(), week), 2);
    }
}
