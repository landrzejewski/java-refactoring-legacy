package pl.training.workshop.m5.s08_composition.step2;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Krok 2 (rozwiązanie): Encapsulate Collection - zamiast delegata klient dostaje niemodyfikowalną kopię
 * w kolejności wyboru. Wąska fasada: tylko operacje, których klienci naprawdę używają.
 * Świadomie tracimy: przypisywalność do Set, remove/clear/retainAll, equals/hashCode zbioru.
 */
public final class SeatSelection {
    private final Set<String> seats = new LinkedHashSet<>();
    private int clicks;

    public boolean add(String seat) {
        clicks++;
        return seats.add(seat);
    }

    public boolean addAll(Collection<? extends String> more) {
        boolean changed = false;
        for (String seat : more) {
            changed |= add(seat);
        }
        return changed;
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

    public List<String> seats() {
        return List.copyOf(seats);
    }
}
