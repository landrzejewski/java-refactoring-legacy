package pl.training.workshop.m6.s17_singleton.start;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import pl.training.workshop.shared.Money;

/**
 * Start: cennik parsowany w konstruktorze. Jest niemutowalny, więc każda instancja jest
 * równoważna - a mimo to klienci tworzą nową przy każdym wywołaniu. Licznik służy do pomiaru.
 */
public final class PriceList {
    private static final String TARIFF = "2D=25.00;3D=32.00;IMAX=40.00";
    private static final AtomicInteger CREATED = new AtomicInteger();

    private final Map<String, Money> prices = new HashMap<>();

    public PriceList() {
        for (String entry : TARIFF.split(";")) {
            String[] pair = entry.split("=");
            prices.put(pair[0], Money.of(pair[1]));
        }
        CREATED.incrementAndGet();
    }

    public static int created() {
        return CREATED.get();
    }

    public Money basePrice(String format) {
        Money price = prices.get(format);
        if (price == null) {
            throw new IllegalArgumentException("unknown format: " + format);
        }
        return price;
    }
}
