package pl.training.workshop.exercises.e02_hallmap.step3;

import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.exercises.e02_hallmap.RowSpec;

/**
 * Krok 3: rząd nie zna już danych kategorii - prosi o nią {@link SeatCategories} i dokłada
 * tylko to, co jest własne dla miejsca: etykietę z numerem.
 */
public final class Row extends HallGroup {
    private final String label;

    private Row(String label, List<Seat> seats) {
        super(seats);
        this.label = label;
    }

    static Row of(RowSpec spec) {
        String layout = spec.layout();
        List<Seat> seats = new ArrayList<>();
        for (int i = 0; i < layout.length(); i++) {
            char code = layout.charAt(i);
            if (code == '_') {
                continue;
            }
            seats.add(new Seat(spec.label() + (i + 1), SeatCategories.of(code)));
        }
        if (seats.isEmpty()) {
            throw new IllegalArgumentException("row " + spec.label() + " has no seats");
        }
        return new Row(spec.label(), seats);
    }

    String label() {
        return label;
    }
}
