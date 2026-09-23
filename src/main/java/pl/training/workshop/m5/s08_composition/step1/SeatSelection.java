package pl.training.workshop.m5.s08_composition.step1;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Krok 1: Replace Inheritance with Delegation (wygenerowane przez IDE). Zbiór jest prywatnym delegatem,
 * addAll() woła addAll() delegata - jego wewnętrzne add() nie trafia już do naszego licznika.
 * Pozostała pułapka delegowania: wygenerowany getter wydaje delegata, więc da się go zmienić z zewnątrz.
 */
public class SeatSelection {
    private final Set<String> seats = new LinkedHashSet<>();
    private int clicks;

    public boolean add(String seat) {
        clicks++;
        return seats.add(seat);
    }

    public boolean addAll(Collection<? extends String> more) {
        clicks += more.size();
        return seats.addAll(more);
    }

    public boolean contains(String seat) {
        return seats.contains(seat);
    }

    public int size() {
        return seats.size();
    }

    public int clicks() {
        return clicks;
    }

    public Set<String> getSeats() {
        return seats;
    }
}
