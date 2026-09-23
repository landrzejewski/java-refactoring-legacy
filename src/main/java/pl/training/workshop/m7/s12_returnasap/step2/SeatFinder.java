package pl.training.workshop.m7.s12_returnasap.step2;

import java.util.List;
import java.util.Optional;

import pl.training.workshop.m7.s12_returnasap.Seat;

/**
 * Krok 2 (rozwiązanie): Return ASAP w pętli - zwracamy tam, gdzie wynik jest ostateczny.
 * Flaga found i zmienna result zniknęły. inspected++ zostaje PRZED return (mutacja zachowana),
 * a size()/get(index) zostają - iterator zmieniłby sposób dostępu do listy.
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
        for (int index = 0; index < seats.size(); index++) {
            Seat seat = seats.get(index);
            inspected++;
            if (seat.row() >= minRow && !seat.taken()) {
                return Optional.of(seat.label());
            }
        }
        return Optional.empty();
    }

    public int inspected() {
        return inspected;
    }
}
