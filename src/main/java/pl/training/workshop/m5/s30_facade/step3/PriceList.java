package pl.training.workshop.m5.s30_facade.step3;

import java.util.Map;

import pl.training.workshop.shared.Money;

/** Krok 3: podsystem ukryty (package-private) - cennik biletu 2D wg typu (N, S, E, C). */
final class PriceList {
    private static final Map<String, Money> PRICES = Map.of(
            "N", Money.of("25.00"), "S", Money.of("18.75"), "E", Money.of("17.50"), "C", Money.of("15.00"));

    Money price(String ticketType) {
        Money price = PRICES.get(ticketType);
        if (price == null) {
            throw new IllegalArgumentException("unknown ticket type: " + ticketType);
        }
        return price;
    }
}
