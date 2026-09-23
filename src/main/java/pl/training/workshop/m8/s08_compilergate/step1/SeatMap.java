package pl.training.workshop.m8.s08_compilergate.step1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Krok 1: typy generyczne - znikają ostrzeżenia rawtypes i unchecked (i rzutowania). */
public final class SeatMap {
    private final Map<Integer, List<String>> seatsByRow = new TreeMap<>();

    public void take(String seat) {
        Integer row = Integer.valueOf(seat.substring(1));
        seatsByRow.computeIfAbsent(row, r -> new ArrayList<>()).add(seat);
    }

    public Map<Integer, Integer> takenPerRow() {
        Map<Integer, Integer> result = new TreeMap<>();
        seatsByRow.forEach((row, seats) -> result.put(row, seats.size()));
        return result;
    }
}
