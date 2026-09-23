package pl.training.workshop.m5.s16_serializationproxy.step3;

import pl.training.workshop.shared.Money;

/**
 * Krok 3: Extract Interface dla integracji - rola, którą kontener DI może opakować dynamicznym proxy
 * (java.lang.reflect.Proxy) bez dziedziczenia po implementacji.
 */
public interface Pricing {
    Money studentPrice(Money basePrice);
}
