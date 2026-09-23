package pl.training.workshop.m4.s06_inlinemethod.step1;

import java.math.BigDecimal;
import java.time.LocalTime;

import pl.training.workshop.m4.s06_inlinemethod.Ticket;

/**
 * Krok 1: Inline Method {@code base} - prywatny, niepolimorficzny delegat z jednym wywołaniem.
 * Nie dodawał znaczenia ponad basePrice, więc to najbezpieczniejszy możliwy Inline.
 */
public class TicketPricing {
    private static final BigDecimal MORNING_REDUCTION = new BigDecimal("5.00");

    public BigDecimal total(Ticket ticket) {
        return addFee(price(ticket));
    }

    public BigDecimal price(Ticket ticket) {
        BigDecimal price = basePrice(ticket.format());
        return ticket.start().isBefore(LocalTime.NOON) ? price.subtract(MORNING_REDUCTION) : price;
    }

    /** Kasa nie pobiera opłaty rezerwacyjnej. Nadpisywane w OnlineTicketPricing. */
    protected BigDecimal bookingFee() {
        return new BigDecimal("0.00");
    }

    private BigDecimal addFee(BigDecimal price) {
        return price.add(bookingFee());
    }

    private static BigDecimal basePrice(int format) {
        return switch (format) {
            case 3 -> new BigDecimal("40.00");
            case 2 -> new BigDecimal("32.00");
            default -> new BigDecimal("25.00");
        };
    }
}
