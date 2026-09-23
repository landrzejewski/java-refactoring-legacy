package pl.training.workshop.m3.s07_srp.step3;

import java.util.List;

import pl.training.workshop.m3.s07_srp.Sale;

/**
 * Krok 3 (rozwiązanie): Extract Class - każda sekcja w klasie swojego aktora.
 * DailyReport tylko składa dokument (koordynuje, nie zna polityk). Zmiana definicji
 * hitu dotyka wyłącznie {@link MarketingSection}.
 */
public final class DailyReport {
    private final AccountingSection accounting = new AccountingSection();
    private final MarketingSection marketing = new MarketingSection();

    public String render(List<Sale> sales) {
        return accounting.render(sales) + marketing.render(sales);
    }
}
