package pl.training.workshop.m5.s16_serializationproxy.step2;

import pl.training.workshop.shared.Money;

/**
 * Krok 2: bez zmian.
 */
public final class TicketPricing {
    public Money studentPrice(Money basePrice) {
        return basePrice.minus(basePrice.percent(25));
    }
}
