package pl.training.workshop.m4.s11_encapsulatecollection.step1;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.m4.s11_encapsulatecollection.Seat;

/**
 * Krok 1: Encapsulate Collection, faza przejściowa - pole prywatne, operacje addSeat/removeSeat
 * w właścicielu, a getter zwraca TĘ SAMĄ listę. Zachowujemy stary alias, więc to nadal czysta
 * refaktoryzacja: klient, który jeszcze modyfikuje listę przez getter, działa jak wcześniej.
 */
public final class Booking {
    private static final BigDecimal PRICE_2D = new BigDecimal("25.00");
    private static final BigDecimal VIP_SURCHARGE = new BigDecimal("10.00");

    private final List<Seat> seats = new ArrayList<>();

    /** Przejściowo: żywa, MODYFIKOWALNA lista - dokładnie to, co dawało publiczne pole. */
    public List<Seat> seats() {
        return seats;
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
