package pl.training.workshop.m7.s12_returnasap.step1;

import java.util.List;
import java.util.Optional;

import pl.training.workshop.m7.s12_returnasap.Seat;

/**
 * Krok 1: guard clauses - w seatClass() przypadki kończące (brak, zajęte) zwracają od razu,
 * zmienna result zniknęła. W firstFree() guard dla null zamiast otaczającego if.
 */
public final class SeatFinder {
    private int inspected;

    public String seatClass(Seat seat, int vipFromRow) {
        if (seat == null) {
            return "BRAK";
        }
        if (seat.taken()) {
            return "ZAJETE";
        }
        if (seat.row() >= vipFromRow) {
            return "VIP";
        }
        return "STANDARD";
    }

    public Optional<String> firstFree(List<Seat> seats, int minRow) {
        if (seats == null) {
            return Optional.empty();
        }
        String result = null;
        boolean found = false;
        int index = 0;
        while (!found && index < seats.size()) {
            Seat seat = seats.get(index);
            inspected++;
            if (seat.row() >= minRow && !seat.taken()) {
                result = seat.label();
                found = true;
            }
            index++;
        }
        return Optional.ofNullable(result);
    }

    public int inspected() {
        return inspected;
    }
}
