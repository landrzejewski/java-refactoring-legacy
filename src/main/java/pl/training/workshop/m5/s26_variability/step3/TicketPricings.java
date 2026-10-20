package pl.training.workshop.m5.s26_variability.step3;

import java.util.Map;

import pl.training.workshop.shared.Money;

/**
 * Krok 3: bez zmian - jedno miejsce wyboru strategii po kodzie typu biletu (N, S, E, C).
 * Strategie są bezstanowe, więc współdzielimy jedne instancje.
 */
public final class TicketPricings {
    private static final Map<String, TicketPricing> BY_TYPE = Map.of(
            "N", base -> base,
            "S", discounted(25),
            "E", discounted(30),
            "C", discounted(40));

    private TicketPricings() {
    }

    public static TicketPricing forType(String code) {
        TicketPricing pricing = BY_TYPE.get(code);
        if (pricing == null) {
            throw new IllegalArgumentException("unknown ticket type: " + code);
        }
        return pricing;
    }

    private static TicketPricing discounted(int percent) {
        return base -> base.minus(base.percent(percent));
    }
}
