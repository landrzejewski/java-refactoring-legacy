package pl.training.workshop.exercises.e02_hallmap.step2;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

import pl.training.workshop.exercises.e02_hallmap.HallSpec;

/**
 * Krok 2: klient nie zna już poziomów sali. Agregaty liczy drzewo, a po miejscach
 * przechodzi zwykłą pętlą for-each - kolejność i pomijanie luk należą do sali.
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
