package pl.training.workshop.m7.s02_methodobject.step2;

import pl.training.workshop.m7.s02_methodobject.GroupOrder;
import pl.training.workshop.m7.s02_methodobject.Quote;

/**
 * Krok 2: fasada bez zmian - publiczne API i sposób tworzenia obiektu metody
 * (nowy na każde wywołanie) zostają takie jak w kroku 1. Zmienia się GroupQuoteCalculation.
 */
public final class GroupPricing {
    public Quote quote(GroupOrder order) {
        return new GroupQuoteCalculation(order).calculate();
    }
}
