package pl.training.workshop.exercises.e02_hallmap.step1;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

import pl.training.workshop.exercises.e02_hallmap.HallSpec;

/**
 * Krok 1: pojemność i dopłaty liczy drzewo. Zliczanie kategorii i lista miejsc nadal
 * wędrują po strukturze ręcznie - klient wciąż zna poziomy sala, sektor, rząd.
 */
public final class HallMaps {
    public String describe(HallSpec spec) {
        Hall hall = Hall.of(spec);

        Map<String, Integer> counts = new LinkedHashMap<>();
        for (Section section : hall.sections()) {
            for (Row row : section.rows()) {
                for (Seat seat : row.seats()) {
                    counts.merge(seat.categoryName(), 1, Integer::sum);
                }
            }
        }

        StringJoiner seats = new StringJoiner(", ");
        for (Section section : hall.sections()) {
            for (Row row : section.rows()) {
                for (Seat seat : row.seats()) {
                    seats.add(seat.label() + " " + seat.icon());
                }
            }
        }

        StringJoiner categories = new StringJoiner(" ");
        counts.forEach((name, count) -> categories.add(name + "=" + count));
        return "Sala " + hall.name() + ": pojemnosc " + hall.capacity() + ", doplaty " + hall.surcharges() + "\n"
                + "Kategorie: " + categories + "\n"
                + "Miejsca: " + seats;
    }
}
