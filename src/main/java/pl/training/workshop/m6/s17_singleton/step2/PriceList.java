package pl.training.workshop.m6.s17_singleton.step2;

import java.util.HashMap;
import java.util.Map;

import pl.training.workshop.shared.Money;

/**
 * Krok 2: singleton jako enum - JVM gwarantuje jedną instancję (na loader klas), bezpieczną
 * publikację i odporność na serializację/refleksję. Mapa niemodyfikowalna po zbudowaniu.
 */
public enum PriceList {
    INSTANCE;

    private static final String TARIFF = "2D=25.00;3D=32.00;IMAX=40.00";

    private final Map<String, Money> prices;

    PriceList() {
        Map<String, Money> parsed = new HashMap<>();
        for (String entry : TARIFF.split(";")) {
            String[] pair = entry.split("=");
            parsed.put(pair[0], Money.of(pair[1]));
        }
        prices = Map.copyOf(parsed);
    }

    public Money basePrice(String format) {
        Money price = prices.get(format);
        if (price == null) {
            throw new IllegalArgumentException("unknown format: " + format);
        }
        return price;
    }
}
