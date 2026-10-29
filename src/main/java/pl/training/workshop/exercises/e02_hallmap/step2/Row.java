package pl.training.workshop.exercises.e02_hallmap.step2;

import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.exercises.e02_hallmap.RowSpec;
import pl.training.workshop.shared.Money;

/** Krok 2: rząd - węzeł nad miejscami; luka nadal nie tworzy dziecka, ale zużywa numer. */
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
            seats.add(seatFor(code, spec.label() + (i + 1)));
        }
        if (seats.isEmpty()) {
            throw new IllegalArgumentException("row " + spec.label() + " has no seats");
        }
        return new Row(spec.label(), seats);
    }

    private static Seat seatFor(char code, String label) {
        return switch (code) {
            case 'N' -> new Seat(label, 'N', "Normalne", Money.ZERO, "o", "Miejsce standardowe");
            case 'V' -> new Seat(label, 'V', "VIP", Money.of("10.00"), "*", "Fotel skorzany z szerszym rozstawem");
            case 'W' -> new Seat(label, 'W', "Wozek", Money.ZERO, "&", "Miejsce dla osoby na wozku");
            case 'D' -> new Seat(label, 'D', "Dwuosobowe", Money.of("6.00"), "=", "Sofa dla dwojga");
            default -> throw new IllegalArgumentException("unknown seat category: " + code);
        };
    }

    String label() {
        return label;
    }
}
