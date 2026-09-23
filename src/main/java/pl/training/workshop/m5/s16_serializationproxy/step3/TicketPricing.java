package pl.training.workshop.m5.s16_serializationproxy.step3;

import pl.training.workshop.shared.Money;

/**
 * Krok 3 (rozwiązanie): implementacja może zostać final - JDK Proxy opakowuje interfejs Pricing.
 * Uwaga: wywołanie this.innaMetoda() wewnątrz klasy i tak omija proxy (self-invocation).
 */
public final class TicketPricing implements Pricing {
    @Override
    public Money studentPrice(Money basePrice) {
        return basePrice.minus(basePrice.percent(25));
    }
}
