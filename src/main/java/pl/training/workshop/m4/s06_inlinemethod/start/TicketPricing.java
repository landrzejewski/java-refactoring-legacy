package pl.training.workshop.m4.s06_inlinemethod.start;

import java.math.BigDecimal;
import java.time.LocalTime;

import pl.training.workshop.m4.s06_inlinemethod.Ticket;

/**
 * Start: cennik kasy. Dwa pośredniki bez znaczenia (base, addFee) i jeden hak
 * nadpisywany w OnlineTicketPricing (bookingFee). Wszystkie trzy "wyglądają" na trywialne.
 */
public class TicketPricing {
    private static final BigDecimal MORNING_REDUCTION = new BigDecimal("5.00");

    public BigDecimal total(Ticket ticket) {
        return addFee(price(ticket));
    }

    public BigDecimal price(Ticket ticket) {
        BigDecimal price = base(ticket);
        return ticket.start().isBefore(LocalTime.NOON) ? price.subtract(MORNING_REDUCTION) : price;
    }

    /** Kasa nie pobiera opłaty rezerwacyjnej. Nadpisywane w OnlineTicketPricing. */
    protected BigDecimal bookingFee() {
        return new BigDecimal("0.00");
    }

    private BigDecimal base(Ticket ticket) {
        return basePrice(ticket.format());
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
