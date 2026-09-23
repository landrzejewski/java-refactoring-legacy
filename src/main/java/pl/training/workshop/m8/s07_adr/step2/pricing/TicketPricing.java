package pl.training.workshop.m8.s07_adr.step2.pricing;

import pl.training.workshop.shared.Money;

/** Krok 2: spełnienie R2 - Money zamiast liczby zmiennoprzecinkowej. Moduł zgodny z ADR-0007. */
public final class TicketPricing {
    private static final int GROUP_SIZE = 10;
    private static final int GROUP_DISCOUNT_PERCENT = 10;

    public Quote total(int tickets, Money unitPrice) {
        Money sum = unitPrice.times(tickets);
        boolean groupDiscount = tickets >= GROUP_SIZE;
        if (groupDiscount) {
            sum = sum.minus(sum.percent(GROUP_DISCOUNT_PERCENT));
        }
        return new Quote(sum, groupDiscount);
    }
}
