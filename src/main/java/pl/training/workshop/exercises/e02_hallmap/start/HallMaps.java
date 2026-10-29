package pl.training.workshop.exercises.e02_hallmap.start;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

import pl.training.workshop.exercises.e02_hallmap.HallSpec;
import pl.training.workshop.shared.Money;

/**
 * Start: klient mapy sali. Cztery razy wykonuje tę samą wędrówkę sala, sektor, rząd, miejsce
 * i za każdym razem sam pomija luki. Nowy poziom (balkon z własnymi sektorami) albo nowe
 * pytanie o salę to kolejna kopia potrójnej pętli.
 */
public final class HallMaps {
    public String describe(HallSpec spec) {
        Hall hall = Hall.of(spec);

        int capacity = 0;
        for (Section section : hall.sections()) {
            for (Row row : section.rows()) {
                for (Seat seat : row.places()) {
                    if (seat == null) {
                        continue;
                    }
                    capacity++;
                }
            }
        }

        Money surcharges = Money.ZERO;
        for (Section section : hall.sections()) {
            for (Row row : section.rows()) {
                for (Seat seat : row.places()) {
                    if (seat == null) {
                        continue;
                    }
                    surcharges = surcharges.plus(seat.surcharge());
                }
            }
        }

        Map<String, Integer> counts = new LinkedHashMap<>();
        for (Section section : hall.sections()) {
            for (Row row : section.rows()) {
                for (Seat seat : row.places()) {
                    if (seat == null) {
                        continue;
                    }
                    counts.merge(seat.categoryName(), 1, Integer::sum);
                }
            }
        }

        StringJoiner seats = new StringJoiner(", ");
        for (Section section : hall.sections()) {
            for (Row row : section.rows()) {
                for (Seat seat : row.places()) {
                    if (seat == null) {
                        continue;
                    }
                    seats.add(seat.label() + " " + seat.icon());
                }
            }
        }

        StringJoiner categories = new StringJoiner(" ");
        counts.forEach((name, count) -> categories.add(name + "=" + count));
        return "Sala " + hall.name() + ": pojemnosc " + capacity + ", doplaty " + surcharges + "\n"
                + "Kategorie: " + categories + "\n"
                + "Miejsca: " + seats;
    }
}
