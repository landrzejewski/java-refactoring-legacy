package pl.training.workshop.m6.s17_singleton.step3;

import java.util.HashMap;
import java.util.Map;

import pl.training.workshop.shared.Money;

/**
 * Krok 3: enum singleton bez zmian, ale implementuje Tariff - to domyślna, nie jedyna
 * implementacja. Cykl życia (jedna instancja) to decyzja korzenia kompozycji.
 */
public enum PriceList implements Tariff {
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

    @Override
    public Money basePrice(String format) {
        Money price = prices.get(format);
        if (price == null) {
            throw new IllegalArgumentException("unknown format: " + format);
        }
        return price;
    }
}
