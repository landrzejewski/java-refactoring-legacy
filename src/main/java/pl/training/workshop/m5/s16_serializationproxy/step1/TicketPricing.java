package pl.training.workshop.m5.s16_serializationproxy.step1;

import pl.training.workshop.shared.Money;

/**
 * Krok 1: bez zmian - nadal final i bez interfejsu (tym zajmiemy się w kroku 3).
 */
public final class TicketPricing {
    public Money studentPrice(Money basePrice) {
        return basePrice.minus(basePrice.percent(25));
    }
}
