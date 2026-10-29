package pl.training.workshop.exercises.e04_seatpicker;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Stabilny kontrakt sceny: układ sali na jeden seans (Kraina Lodu 3D): liczba rzędów i miejsc,
 * pierwszy rząd VIP oraz miejsca już sprzedane.
 */
public record HallPlan(int rows, int seatsPerRow, int vipFromRow, Set<Seat> taken) {
    /** Sala 12 x 10, VIP od rzędu 10, kilka miejsc sprzedanych. */
    public static HallPlan standard() {
        return new HallPlan(12, 10, 10, Set.of(
                new Seat(6, 5), new Seat(6, 6), new Seat(7, 1), new Seat(7, 2), new Seat(10, 3)));
    }

    /** Mała, prawie pełna sala 3 x 4: wolne są tylko pojedyncze miejsca, nigdzie dwa obok siebie. */
    public static HallPlan almostFull() {
        return new HallPlan(3, 4, 3, Set.of(
                new Seat(1, 2), new Seat(1, 4),
                new Seat(2, 1), new Seat(2, 3),
                new Seat(3, 2), new Seat(3, 3)));
    }

    public boolean exists(Seat seat) {
        return seat.row() >= 1 && seat.row() <= rows && seat.number() >= 1 && seat.number() <= seatsPerRow;
    }

    public boolean isTaken(Seat seat) {
        return taken.contains(seat);
    }

    public boolean isVip(Seat seat) {
        return seat.row() >= vipFromRow;
    }

    /** Rzędy w kolejności preferencji: od środka sali na zewnątrz, np. 6, 7, 5, 8, 4, ... */
    public List<Integer> rowsFromMiddle() {
        List<Integer> order = new ArrayList<>();
        int middle = (rows + 1) / 2;
        for (int offset = 0; order.size() < rows; offset++) {
            if (middle + offset <= rows && !order.contains(middle + offset)) {
                order.add(middle + offset);
            }
            if (middle - offset >= 1 && !order.contains(middle - offset)) {
                order.add(middle - offset);
            }
        }
        return order;
    }
}
