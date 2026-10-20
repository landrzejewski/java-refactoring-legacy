package pl.training.workshop.m5.s30_facade.step3;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Krok 3: podsystem ukryty (package-private) - zajętość miejsc, rezerwacja atomowa.
 * Dostęp tylko przez BookingFacade; w prawdziwym kodzie kanały sprzedaży leżałyby w innym pakiecie.
 */
final class SeatRegistry {
    private final Map<String, Set<String>> taken = new TreeMap<>();

    void reserve(String screening, List<String> seats) {
        Set<String> current = taken.computeIfAbsent(screening, key -> new TreeSet<>());
        for (String seat : seats) {
            if (current.contains(seat)) {
                throw new IllegalStateException("seat taken " + seat);
            }
        }
        current.addAll(seats);
    }

    void release(String screening, List<String> seats) {
        Set<String> current = taken.get(screening);
        if (current != null) {
            seats.forEach(current::remove);
        }
    }

    String describe() {
        return taken.toString();
    }
}
