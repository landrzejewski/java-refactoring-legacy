package pl.training.workshop.exercises.e02_hallmap.step1;

import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.exercises.e02_hallmap.RowSpec;
import pl.training.workshop.shared.Money;

/**
 * Krok 1: rząd jest węzłem drzewa nad miejscami. Luka nie jest już {@code null} w tablicy:
 * przejście po prostu nie tworzy dziecka, a numer miejsca nadal wynika z pozycji w układzie.
 */
public final class Row extends HallGroup {
    private final String label;
    private final List<Seat> seats;

    private Row(String label, List<Seat> seats) {
        super(seats);
        this.label = label;
        this.seats = List.copyOf(seats);
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

    List<Seat> seats() {
        return seats;
    }
}
