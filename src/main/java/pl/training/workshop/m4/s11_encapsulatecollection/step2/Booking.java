package pl.training.workshop.m4.s11_encapsulatecollection.step2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.training.workshop.m4.s11_encapsulatecollection.Seat;

/**
 * Krok 2 (zmiana kontraktu): getter zwraca niemodyfikowalny WIDOK.
 * Klient nie zmieni członkostwa (UnsupportedOperationException), ale widzi późniejsze zmiany
 * właściciela. Bezpieczne dopiero, gdy żaden klient nie modyfikuje listy przez getter.
 */
public final class Booking {
    private static final BigDecimal PRICE_2D = new BigDecimal("25.00");
    private static final BigDecimal VIP_SURCHARGE = new BigDecimal("10.00");

    private final List<Seat> seats = new ArrayList<>();

    /** Żywy widok tylko do odczytu. */
    public List<Seat> seats() {
        return Collections.unmodifiableList(seats);
    }

    public void addSeat(Seat seat) {
        seats.add(seat);
    }

    public void removeSeat(Seat seat) {
        seats.remove(seat);
    }

    public BigDecimal total() {
        BigDecimal total = BigDecimal.ZERO.setScale(2);
        for (Seat seat : seats) {
            total = total.add(seat.row() >= 10 ? PRICE_2D.add(VIP_SURCHARGE) : PRICE_2D);
        }
        return total;
    }
}
