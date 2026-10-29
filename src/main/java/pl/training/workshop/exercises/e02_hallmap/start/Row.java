package pl.training.workshop.exercises.e02_hallmap.start;

import pl.training.workshop.exercises.e02_hallmap.RowSpec;
import pl.training.workshop.shared.Money;

/**
 * Start: rząd trzyma tablicę miejsc, w której przejście (luka) to {@code null}. Każdy, kto
 * przechodzi po rzędzie, musi o tym pamiętać. Dane kategorii (nazwa, dopłata, ikona, opis)
 * są kopiowane do każdego miejsca z osobna - w sali IMAX na 400 miejsc to 400 kopii.
 */
public final class Row {
    private final String label;
    private final Seat[] places;

    private Row(String label, Seat[] places) {
        this.label = label;
        this.places = places;
    }

    static Row of(RowSpec spec) {
        String layout = spec.layout();
        Seat[] places = new Seat[layout.length()];
        int seats = 0;
        for (int i = 0; i < layout.length(); i++) {
            char code = layout.charAt(i);
            if (code == '_') {
                continue;
            }
            places[i] = seatFor(code, spec.label() + (i + 1));
            seats++;
        }
        if (seats == 0) {
            throw new IllegalArgumentException("row " + spec.label() + " has no seats");
        }
        return new Row(spec.label(), places);
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

    /** Tablica z lukami: {@code null} oznacza przejście. */
    Seat[] places() {
        return places;
    }
}
