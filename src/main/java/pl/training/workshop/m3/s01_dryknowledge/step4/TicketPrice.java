package pl.training.workshop.m3.s01_dryknowledge.step4;

import java.math.BigDecimal;
import java.math.RoundingMode;

import pl.training.workshop.m3.s01_dryknowledge.Ticket;

/**
 * Krok 4: jedyna, autorytatywna reprezentacja wiedzy "ile kosztuje bilet".
 * Właściciel: dział cennika. Sprzedaż i zwrot tylko z niej korzystają.
 */
public final class TicketPrice {
    private static final BigDecimal MORNING_DISCOUNT = new BigDecimal("5.00");

    public BigDecimal of(Ticket ticket) {
        BigDecimal base = basePrice(ticket.format());
        BigDecimal price = base.subtract(base.multiply(BigDecimal.valueOf(discountPercent(ticket.type())))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
        if (ticket.start().getHour() < 12) {
            price = price.subtract(MORNING_DISCOUNT);
        }
        return price.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal basePrice(String format) {
        return switch (format) {
            case "IMAX" -> new BigDecimal("40.00");
            case "3D" -> new BigDecimal("32.00");
            default -> new BigDecimal("25.00");
        };
    }

    private int discountPercent(String type) {
        return switch (type) {
            case "STUDENT" -> 25;
            case "SENIOR" -> 30;
            case "CHILD" -> 40;
            default -> 0;
        };
    }
}
