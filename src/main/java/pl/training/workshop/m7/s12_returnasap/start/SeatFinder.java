package pl.training.workshop.m7.s12_returnasap.start;

import java.util.List;
import java.util.Optional;

import pl.training.workshop.m7.s12_returnasap.Seat;

/**
 * Start: "jeden punkt wyjścia" - wynik niesiony w zmiennej result i fladze found,
 * zagnieżdżone if-y. Licznik inspected (metryka dla działu IT) jest efektem ubocznym,
 * który musi przetrwać każdą zmianę.
 */
public final class SeatFinder {
    private int inspected;

    public String seatClass(Seat seat, int vipFromRow) {
        String result = "STANDARD";
        if (seat != null) {
            if (!seat.taken()) {
                if (seat.row() >= vipFromRow) {
                    result = "VIP";
                }
            } else {
                result = "ZAJETE";
            }
        } else {
            result = "BRAK";
        }
        return result;
    }

    public Optional<String> firstFree(List<Seat> seats, int minRow) {
        String result = null;
        if (seats != null) {
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
        }
        return Optional.ofNullable(result);
    }

    public int inspected() {
        return inspected;
    }
}
