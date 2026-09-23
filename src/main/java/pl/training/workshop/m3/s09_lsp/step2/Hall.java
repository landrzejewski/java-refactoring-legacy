package pl.training.workshop.m3.s09_lsp.step2;

import java.util.Set;
import java.util.TreeSet;

/**
 * Krok 2: Hall jest final - nikt już nie dziedziczy, żeby "wyłączyć" rezerwację.
 * Chroniony konstruktor dla podklasy zniknął.
 * <p>Kontrakt {@link #reserve}: dla wolnego miejsca z zakresu rezerwuje je - potem
 * {@code isFree(seat) == false}, a {@code freeSeats()} maleje o 1. Zajęte miejsce:
 * {@link IllegalStateException}.
 */
public final class Hall implements SeatMap {
    private final int capacity;
    private final Set<Integer> taken = new TreeSet<>();

    public Hall(int capacity) {
        this.capacity = capacity;
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
