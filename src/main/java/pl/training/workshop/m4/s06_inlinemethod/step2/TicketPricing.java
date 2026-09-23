package pl.training.workshop.m4.s06_inlinemethod.step2;

import java.math.BigDecimal;
import java.time.LocalTime;

import pl.training.workshop.m4.s06_inlinemethod.Ticket;

/**
 * Krok 2 (rozwiązanie): Inline Method {@code addFee}. Wklejamy WYWOŁANIE {@code bookingFee()},
 * więc dynamiczna dyspozycja zostaje i OnlineTicketPricing nadal dolicza 2.00.
 * {@code bookingFee} NIE inline'ujemy: wklejenie jego ciała z klasy bazowej zabiłoby nadpisanie.
 */
public class TicketPricing {
    private static final BigDecimal MORNING_REDUCTION = new BigDecimal("5.00");

    public BigDecimal total(Ticket ticket) {
        return price(ticket).add(bookingFee());
    }

    public BigDecimal price(Ticket ticket) {
        BigDecimal price = basePrice(ticket.format());
        return ticket.start().isBefore(LocalTime.NOON) ? price.subtract(MORNING_REDUCTION) : price;
    }

    /** Kasa nie pobiera opłaty rezerwacyjnej. Nadpisywane w OnlineTicketPricing. */
    protected BigDecimal bookingFee() {
        return new BigDecimal("0.00");
    }

    private static BigDecimal basePrice(int format) {
        return switch (format) {
            case 3 -> new BigDecimal("40.00");
            case 2 -> new BigDecimal("32.00");
            default -> new BigDecimal("25.00");
        };
    }
}
