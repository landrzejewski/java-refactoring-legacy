package pl.training.workshop.m8.s08_compilergate.start;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Start: mapa zajętych miejsc na surowych typach (rawtypes) i niesprawdzanych operacjach (unchecked). */
public final class SeatMap {
    private final Map seatsByRow = new TreeMap();

    public void take(String seat) {
        Integer row = Integer.valueOf(seat.substring(1));
        List seats = (List) seatsByRow.get(row);
        if (seats == null) {
            seats = new ArrayList();
            seatsByRow.put(row, seats);
        }
        seats.add(seat);
    }

    public Map<Integer, Integer> takenPerRow() {
        Map<Integer, Integer> result = new TreeMap<>();
        for (Object row : seatsByRow.keySet()) {
            result.put((Integer) row, ((List) seatsByRow.get(row)).size());
        }
        return result;
    }
}
