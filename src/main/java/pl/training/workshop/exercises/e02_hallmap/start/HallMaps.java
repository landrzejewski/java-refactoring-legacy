package pl.training.workshop.exercises.e02_hallmap.start;

import pl.training.workshop.exercises.e02_hallmap.HallSpec;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Start: klient mapy sali. Cztery razy wykonuje tę samą wędrówkę sala, sektor, rząd, miejsce
 * i za każdym razem sam pomija luki. Nowy poziom (balkon z własnymi sektorami) albo nowe
 * pytanie o salę to kolejna kopia potrójnej pętli.
 */
public final class HallMaps {
    public String describe(HallSpec spec) {
        Hall hall = Hall.of(spec);

        Map<String, Integer> counts = new LinkedHashMap<>();
        StringJoiner seats = new StringJoiner(", ");
        for (Seat seat : hall) {
            counts.merge(seat.categoryName(), 1, Integer::sum);
            seats.add(seat.label() + " " + seat.icon());
        }

        StringJoiner categories = new StringJoiner(" ");
        counts.forEach((name, count) -> categories.add(name + "=" + count));
        return "Sala " + hall.name() + ": pojemnosc " + hall.capacity() + ", doplaty " + hall.surcharges() + "\n"
                + "Kategorie: " + categories + "\n"
                + "Miejsca: " + seats;
    }
}
