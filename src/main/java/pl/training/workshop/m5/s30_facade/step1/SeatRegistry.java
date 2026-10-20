package pl.training.workshop.m5.s30_facade.step1;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/** Podsystem: zajętość miejsc. Rezerwacja jest atomowa - albo wszystkie miejsca, albo żadne. */
public final class SeatRegistry {
    private final Map<String, Set<String>> taken = new TreeMap<>();

    public void reserve(String screening, List<String> seats) {
        Set<String> current = taken.computeIfAbsent(screening, key -> new TreeSet<>());
        for (String seat : seats) {
            if (current.contains(seat)) {
                throw new IllegalStateException("seat taken " + seat);
            }
        }
        current.addAll(seats);
    }

    public void release(String screening, List<String> seats) {
        Set<String> current = taken.get(screening);
        if (current != null) {
            seats.forEach(current::remove);
        }
    }

    public String describe() {
        return taken.toString();
    }
}
