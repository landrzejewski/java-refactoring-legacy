package pl.training.workshop.m3.s09_lsp.step1;

import java.util.Set;
import java.util.TreeSet;

/**
 * Krok 1: Hall implementuje SeatMap. Sala kinowa z miejscami 1..capacity.
 * <p>Kontrakt {@link #reserve}: dla wolnego miejsca z zakresu rezerwuje je - potem
 * {@code isFree(seat) == false}, a {@code freeSeats()} maleje o 1. Zajęte miejsce:
 * {@link IllegalStateException}. Nie ma warunku "ta sala może odmówić".
 */
public class Hall implements SeatMap {
    private final int capacity;
    private final Set<Integer> taken = new TreeSet<>();

    public Hall(int capacity) {
        this.capacity = capacity;
    }

    protected Hall(int capacity, Set<Integer> alreadyTaken) {
        this.capacity = capacity;
        this.taken.addAll(alreadyTaken);
    }

    public void reserve(int seat) {
        if (seat < 1 || seat > capacity) {
            throw new IllegalArgumentException("brak miejsca " + seat);
        }
        if (!taken.add(seat)) {
            throw new IllegalStateException("miejsce zajete: " + seat);
        }
    }

    @Override
    public boolean isFree(int seat) {
        return seat >= 1 && seat <= capacity && !taken.contains(seat);
    }

    @Override
    public int freeSeats() {
        return capacity - taken.size();
    }

    @Override
    public int capacity() {
        return capacity;
    }
}
