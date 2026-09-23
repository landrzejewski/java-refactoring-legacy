package pl.training.workshop.m3.s04_drytests;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Kod produkcyjny sceny (stabilny): taryfa - ceny formatów i zniżki typów biletów.
 * {@link #withDiscount} pozwala testom podłożyć taryfę z błędem.
 */
public record Tariff(Map<String, BigDecimal> basePrices, Map<String, Integer> discountPercents) {
    public Tariff {
        basePrices = Map.copyOf(basePrices);
        discountPercents = Map.copyOf(discountPercents);
    }

    public static Tariff standard() {
        return new Tariff(
                Map.of("2D", new BigDecimal("25.00"),
                        "3D", new BigDecimal("32.00"),
                        "IMAX", new BigDecimal("40.00")),
                Map.of("NORMAL", 0, "STUDENT", 25, "SENIOR", 30, "CHILD", 40));
    }

    public Tariff withDiscount(String type, int percent) {
        Map<String, Integer> changed = new HashMap<>(discountPercents);
        changed.put(type, percent);
        return new Tariff(basePrices, changed);
    }
}
