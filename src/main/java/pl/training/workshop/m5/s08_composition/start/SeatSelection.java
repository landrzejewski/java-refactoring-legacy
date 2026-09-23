package pl.training.workshop.m5.s08_composition.start;

import java.io.Serial;
import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * Start: wybór miejsc dziedziczy po LinkedHashSet tylko po to, by mieć add/contains za darmo,
 * i liczy kliknięcia do analityki. Pułapka self-use: odziedziczone addAll() woła add() na this,
 * więc miejsca dodane hurtem liczą się podwójnie. Do tego klient dostaje całe API zbioru
 * (remove, clear, retainAll...), które omija licznik.
 */
public class SeatSelection extends LinkedHashSet<String> {
    @Serial
    private static final long serialVersionUID = 1L;

    private int clicks;

    @Override
    public boolean add(String seat) {
        clicks++;
        return super.add(seat);
    }

    @Override
    public boolean addAll(Collection<? extends String> seats) {
        clicks += seats.size();
        return super.addAll(seats);
    }

    public int clicks() {
        return clicks;
    }
}
