package pl.training.workshop.m8.s10_qualitygate.sample.dirty;

import java.util.ArrayList;
import java.util.List;

/**
 * Próbka "brudnego" kodu domeny dla bramki: znacznik do zrobienia, wydruk na konsolę,
 * surowy typ i metody bez testu.
 */
public final class PriceTable {
    private final List lookups = new ArrayList();

    public int basePrice(String format) {
        // TODO dodać 4DX
        return switch (format) {
            case "IMAX" -> 40;
            case "3D" -> 32;
            default -> 25;
        };
    }

    public int vipSurcharge(int row) {
        System.out.println("VIP? rzad " + row);
        return row >= 10 ? 10 : 0;
    }

    public int lookupCount() {
        return lookups.size();
    }
}
