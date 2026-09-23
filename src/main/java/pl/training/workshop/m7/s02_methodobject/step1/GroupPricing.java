package pl.training.workshop.m7.s02_methodobject.step1;

import pl.training.workshop.m7.s02_methodobject.GroupOrder;
import pl.training.workshop.m7.s02_methodobject.Quote;

/**
 * Krok 1: Extract Method Object - ciało metody skopiowane bez upraszczania
 * do GroupQuoteCalculation. Publiczna metoda zostaje jako fasada i tylko deleguje.
 */
public final class GroupPricing {
    public Quote quote(GroupOrder order) {
        return new GroupQuoteCalculation(order).calculate();
    }
}
