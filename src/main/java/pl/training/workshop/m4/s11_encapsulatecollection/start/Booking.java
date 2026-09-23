package pl.training.workshop.m4.s11_encapsulatecollection.start;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.m4.s11_encapsulatecollection.Seat;

/**
 * Start: publiczna, mutowalna lista miejsc. {@code final} blokuje tylko przypisanie -
 * każdy może dodać, usunąć albo wyczyścić miejsca z pominięciem właściciela.
 */
public final class Booking {
    private static final BigDecimal PRICE_2D = new BigDecimal("25.00");
    private static final BigDecimal VIP_SURCHARGE = new BigDecimal("10.00");

    public final List<Seat> seats = new ArrayList<>();

    public BigDecimal total() {
        BigDecimal total = BigDecimal.ZERO.setScale(2);
        for (Seat seat : seats) {
            total = total.add(seat.row() >= 10 ? PRICE_2D.add(VIP_SURCHARGE) : PRICE_2D);
        }
        return total;
    }
}
