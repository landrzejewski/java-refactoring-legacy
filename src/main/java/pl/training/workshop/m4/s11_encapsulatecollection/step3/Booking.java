package pl.training.workshop.m4.s11_encapsulatecollection.step3;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.m4.s11_encapsulatecollection.Seat;

/**
 * Krok 3 (zmiana kontraktu): getter zwraca MIGAWKĘ ({@code List.copyOf}).
 * Klient nie zmieni członkostwa i NIE widzi późniejszych zmian - dostaje stan z chwili wywołania.
 * Wybór między widokiem a migawką to decyzja o kontrakcie, nie szczegół implementacji.
 */
public final class Booking {
    private static final BigDecimal PRICE_2D = new BigDecimal("25.00");
    private static final BigDecimal VIP_SURCHARGE = new BigDecimal("10.00");

    private final List<Seat> seats = new ArrayList<>();

    /** Niemodyfikowalna kopia z chwili wywołania. */
    public List<Seat> seats() {
        return List.copyOf(seats);
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
