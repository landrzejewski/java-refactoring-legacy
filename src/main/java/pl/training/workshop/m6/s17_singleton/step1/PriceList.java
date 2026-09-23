package pl.training.workshop.m6.s17_singleton.step1;

import java.util.HashMap;
import java.util.Map;

import pl.training.workshop.shared.Money;

/**
 * Krok 1: Limit Instantiation with Singleton (forma klasyczna) - prywatny konstruktor,
 * jedna instancja tworzona przy inicjalizacji klasy. Bezpieczne, bo obiekt jest niemutowalny.
 */
public final class PriceList {
    private static final String TARIFF = "2D=25.00;3D=32.00;IMAX=40.00";
    private static final PriceList INSTANCE = new PriceList();

    private final Map<String, Money> prices = new HashMap<>();

    private PriceList() {
        for (String entry : TARIFF.split(";")) {
            String[] pair = entry.split("=");
            prices.put(pair[0], Money.of(pair[1]));
        }
    }

    public static PriceList getInstance() {
        return INSTANCE;
    }

    public Money basePrice(String format) {
        Money price = prices.get(format);
        if (price == null) {
            throw new IllegalArgumentException("unknown format: " + format);
        }
        return price;
    }
}
